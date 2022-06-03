package com.bangkit.sibisa.ui.quiz

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.*
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.util.Size
import android.view.View
import android.view.ViewGroup
import androidx.camera.core.AspectRatio
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import com.bangkit.sibisa.databinding.ActivityQuizBinding
import com.bangkit.sibisa.models.detection.DetectionResult
import com.bangkit.sibisa.ui.finish.FinishActivity
import com.bangkit.sibisa.utils.showToast
import org.tensorflow.lite.DataType
import org.tensorflow.lite.support.common.ops.NormalizeOp
import org.tensorflow.lite.support.image.ImageProcessor
import org.tensorflow.lite.support.image.TensorImage
import org.tensorflow.lite.support.image.ops.ResizeOp
import org.tensorflow.lite.support.image.ops.ResizeWithCropOrPadOp
import org.tensorflow.lite.support.image.ops.Rot90Op
import org.tensorflow.lite.task.vision.detector.Detection
import org.tensorflow.lite.task.vision.detector.ObjectDetector
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import kotlin.math.min
import kotlin.random.Random

/** Activity that displays the camera and performs object detection on the incoming frames */
class QuizActivity : AppCompatActivity() {

    private lateinit var binding: ActivityQuizBinding

    private lateinit var bitmapBuffer: Bitmap

    private val executor = Executors.newSingleThreadExecutor()
    private val permissions = listOf(Manifest.permission.CAMERA)
    private val permissionsRequestCode = Random.nextInt(0, 10000)

    private var lensFacing: Int = CameraSelector.LENS_FACING_FRONT

    private var pauseAnalysis = false
    private var imageRotationDegrees: Int = 0
    private val tfImageBuffer = TensorImage(DataType.UINT8)

    private lateinit var questions: ArrayList<String>

    private val tfImageProcessor by lazy {
        val cropSize = minOf(bitmapBuffer.width, bitmapBuffer.height)
        ImageProcessor.Builder()
            .add(ResizeWithCropOrPadOp(cropSize, cropSize))
            .add(
                ResizeOp(
                    tfInputSize.height, tfInputSize.width, ResizeOp.ResizeMethod.NEAREST_NEIGHBOR
                )
            )
            .add(Rot90Op(-imageRotationDegrees / 90))
            .add(NormalizeOp(0f, 1f))
            .build()
    }

    private val tfInputSize by lazy {
        Size(320, 320) // Order of axis is: {1, height, width, 3}
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityQuizBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.skipButton.setOnClickListener {
            val intent = Intent(this, FinishActivity::class.java)
            intent.putExtra(FinishActivity.IS_SUCCESS, false)
            startActivity(intent, null)
            finish()
        }

        questions = intent.getStringArrayListExtra(QUESTIONS)!!

        Log.d("QUESTIONS", questions.toString())

        binding.skipButton.setOnClickListener {
            val intent = Intent(this, FinishActivity::class.java)
            intent.putExtra(FinishActivity.IS_SUCCESS, false)
            intent.putExtra(FinishActivity.FROM_LEVEL, LEVEL)
            startActivity(intent)
            finish()
        }

        setupUI()
    }

    private fun setupUI() {
        binding.textQuestionSwitcher.setInAnimation(
            this,
            androidx.appcompat.R.anim.abc_slide_in_bottom
        )
        binding.textQuestionSwitcher.setOutAnimation(
            this,
            androidx.appcompat.R.anim.abc_slide_out_top
        )
        showQuestion()
    }

    private fun showQuestion() {
        binding.textQuestionSwitcher.setText(questions[0].uppercase())
    }

    override fun onDestroy() {

        // Terminate all outstanding analyzing jobs (if there is any).
        executor.apply {
            shutdown()
            awaitTermination(1000, TimeUnit.MILLISECONDS)
        }

        super.onDestroy()
    }

    /** Declare and bind preview and analysis use cases */
    @SuppressLint("UnsafeExperimentalUsageError")
    private fun bindCameraUseCases() = binding.viewFinder.post {

        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)
        cameraProviderFuture.addListener({

            // Camera provider is now guaranteed to be available
            val cameraProvider = cameraProviderFuture.get()

            // Set up the view finder use case to display camera preview
            val preview = Preview.Builder()
                .setTargetAspectRatio(AspectRatio.RATIO_4_3)
                .setTargetRotation(binding.viewFinder.display.rotation)
                .build()

            // Set up the image analysis use case which will process frames in real time
            val imageAnalysis = ImageAnalysis.Builder()
                .setTargetAspectRatio(AspectRatio.RATIO_4_3)
                .setTargetRotation(binding.viewFinder.display.rotation)
                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                .setOutputImageFormat(ImageAnalysis.OUTPUT_IMAGE_FORMAT_RGBA_8888)
                .build()

            imageAnalysis.setAnalyzer(executor, ImageAnalysis.Analyzer { image ->
                if (!::bitmapBuffer.isInitialized) {
                    // The image rotation and RGB image buffer are initialized only once
                    // the analyzer has started running
                    imageRotationDegrees = image.imageInfo.rotationDegrees
                    bitmapBuffer = Bitmap.createBitmap(
                        image.width, image.height, Bitmap.Config.ARGB_8888
                    )
                }

                // Early exit: image analysis is in paused state
                if (pauseAnalysis) {
                    image.close()
                    return@Analyzer
                }

                // Copy out RGB bits to our shared buffer
                image.use { bitmapBuffer.copyPixelsFromBuffer(image.planes[0].buffer) }

                // Process the image in Tensorflow
                val tfImage = tfImageProcessor.process(tfImageBuffer.apply { load(bitmapBuffer) })

                // Step 2: Initialize the detector object
                val options = ObjectDetector.ObjectDetectorOptions.builder()
                    .setMaxResults(5)
                    .setScoreThreshold(ACCURACY_THRESHOLD)
                    .build()

                Log.d("LEVEL", intent.getIntExtra(LEVEL, 1).toString())
                val modelPath = when (intent.getIntExtra(LEVEL, 1)) {
                    1 -> LV1_MODEL_PATH
                    2 -> LV2_MODEL_PATH
                    3 -> LV3_MODEL_PATH
                    else -> LV1_MODEL_PATH
                }

                val detector = ObjectDetector.createFromFileAndOptions(
                    this,
                    modelPath,
                    options
                )

                // Step 3: Feed given image to the detector
                val results = detector.detect(tfImage)

                // Step 4: Parse the detection result and show it
                debugPrint(results)

                val bestResult = results.maxByOrNull {
                    it.categories.first().score
                }

                val resultToDisplay = bestResult?.let {
                    DetectionResult(
                        it.boundingBox,
                        displayText = "${bestResult.categories.first().label}, ${
                            bestResult.categories.first().score.times(
                                100
                            ).toInt()
                        }%",
                        score = bestResult.categories.first().score,
                        predictionText = bestResult.categories.first().label
                    )
                }

                Log.d(TAG, questions[0])

                reportPrediction(resultToDisplay)
                resultToDisplay?.let { checkAnswer(it) }
                checkQuiz()
            })

            // Create a new camera selector each time, enforcing lens facing
            val cameraSelector = CameraSelector.Builder().requireLensFacing(lensFacing).build()

            // Apply declared configs to CameraX using the same lifecycle owner
            cameraProvider.unbindAll()
            cameraProvider.bindToLifecycle(
                this as LifecycleOwner, cameraSelector, preview, imageAnalysis
            )

            // Use the camera object to link our preview use case with the view
            preview.setSurfaceProvider(binding.viewFinder.surfaceProvider)

        }, ContextCompat.getMainExecutor(this))
    }

    private fun debugPrint(results: List<Detection>) {
        for ((i, obj) in results.withIndex()) {
            val box = obj.boundingBox

            Log.d(TAG, "Detected object: ${i} ")
            Log.d(TAG, "  boundingBox: (${box.left}, ${box.top}) - (${box.right},${box.bottom})")

            for ((j, category) in obj.categories.withIndex()) {
                Log.d(TAG, "    Label $j: ${category.label}")
                val confidence: Int = category.score.times(100).toInt()
                Log.d(TAG, "    Confidence: ${confidence}%")
            }
        }
    }

    private fun checkAnswer(result: DetectionResult) {
        runOnUiThread {
            if (result.predictionText.equals(questions[0], true) && result.score >= 0.85f) {
                Log.d("RESULT", "BENARRRR")
                showToast(this, "Benar! ${questions[0]} terdeteksi")
                questions.removeAt(0)
                showQuestion()
            }
        }
    }

    private fun checkQuiz() {
        if (questions.isEmpty()) {
            binding.textQuestionSwitcher.setText("Selamat! Anda berhasil melewati kuis ini")

            val intent = Intent(this, FinishActivity::class.java)
            intent.putExtra(FinishActivity.IS_SUCCESS, true)
            intent.putExtra(FinishActivity.FROM_LEVEL, LEVEL)
            startActivity(intent)
            finish()
        }
    }

    private fun reportPrediction(
        detectionResult: DetectionResult?
    ) = binding.viewFinder.post {

        // score already checked using max threshold
        if (detectionResult == null) {
            binding.boxPrediction.visibility = View.GONE
            binding.textPrediction.visibility = View.GONE
            return@post
        }

        // Location has to be mapped to our local coordinates
        val location = mapOutputCoordinates(detectionResult.boundingBox)

        // Update the text and UI
        binding.textPrediction.text =
            detectionResult.displayText
        (binding.boxPrediction.layoutParams as ViewGroup.MarginLayoutParams).apply {
            topMargin = location.top.toInt()
            leftMargin = location.left.toInt()
            width = min(
                binding.viewFinder.width,
                location.right.toInt() - location.left.toInt()
            )
            height = min(
                binding.viewFinder.height,
                location.bottom.toInt() - location.top.toInt()
            )
        }

        // Make sure all UI elements are visible
        binding.boxPrediction.visibility = View.VISIBLE
        binding.textPrediction.visibility = View.VISIBLE
    }

    /**
     * Helper function used to map the coordinates for objects coming out of
     * the model into the coordinates that the user sees on the screen.
     */
    private fun mapOutputCoordinates(location: RectF): RectF {

        // Step 1: map location to the preview coordinates
        val previewLocation = RectF(
            location.left * binding.viewFinder.width,
            location.top * binding.viewFinder.height,
            location.right * binding.viewFinder.width,
            location.bottom * binding.viewFinder.height
        )

        // Step 2: compensate for camera sensor orientation and mirroring
        val isFrontFacing = lensFacing == CameraSelector.LENS_FACING_FRONT
        val correctedLocation = if (isFrontFacing) {
            RectF(
                binding.viewFinder.width - previewLocation.right,
                previewLocation.top,
                binding.viewFinder.width - previewLocation.left,
                previewLocation.bottom
            )
        } else {
            previewLocation
        }

        // Step 3: compensate for 1:1 to 4:3 aspect ratio conversion + small margin
        val margin = 0.1f
        val requestedRatio = 4f / 3f
        val midX = (correctedLocation.left + correctedLocation.right) / 2f
        val midY = (correctedLocation.top + correctedLocation.bottom) / 2f
        return if (binding.viewFinder.width < binding.viewFinder.height) {
            RectF(
                midX - (1f + margin) * requestedRatio * correctedLocation.width() / 2f,
                midY - (1f - margin) * correctedLocation.height() / 2f,
                midX + (1f + margin) * requestedRatio * correctedLocation.width() / 2f,
                midY + (1f - margin) * correctedLocation.height() / 2f
            )
        } else {
            RectF(
                midX - (1f - margin) * correctedLocation.width() / 2f,
                midY - (1f + margin) * requestedRatio * correctedLocation.height() / 2f,
                midX + (1f - margin) * correctedLocation.width() / 2f,
                midY + (1f + margin) * requestedRatio * correctedLocation.height() / 2f
            )
        }
    }

    override fun onResume() {
        super.onResume()

        // Request permissions each time the app resumes, since they can be revoked at any time
        if (!hasPermissions(this)) {
            ActivityCompat.requestPermissions(
                this, permissions.toTypedArray(), permissionsRequestCode
            )
        } else {
            bindCameraUseCases()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == permissionsRequestCode && hasPermissions(this)) {
            bindCameraUseCases()
        } else {
            finish() // If we don't have the required permissions, we can't run
        }
    }

    /** Convenience method used to check if all permissions required by this app are granted */
    private fun hasPermissions(context: Context) = permissions.all {
        ContextCompat.checkSelfPermission(context, it) == PackageManager.PERMISSION_GRANTED
    }

    companion object {
        private val TAG = QuizActivity::class.java.simpleName

        private const val ACCURACY_THRESHOLD = 0.0f
        private const val LV1_MODEL_PATH = "level1.tflite"
        private const val LV2_MODEL_PATH = "level2.tflite"
        private const val LV3_MODEL_PATH = "level3.tflite"

        const val LEVEL = "level"
        const val QUESTIONS = "questions"
    }
}


