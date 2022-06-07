package com.bangkit.sibisa.ui.quiz

import android.Manifest
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.*
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
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
import com.bangkit.sibisa.R
import com.bangkit.sibisa.databinding.ActivityQuizBinding
import com.bangkit.sibisa.databinding.CustomAlertDialogBinding
import com.bangkit.sibisa.models.detection.DetectionResult
import com.bangkit.sibisa.models.quiz.QuizInfo
import com.bangkit.sibisa.models.quiz.QuizQuestion
import com.bangkit.sibisa.ui.finish.FinishActivity
import com.bangkit.sibisa.utils.showHelpDialog
import com.bangkit.sibisa.utils.showToast
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
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
import kotlin.properties.Delegates
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

    private lateinit var info: QuizInfo
    private lateinit var quizzes: ArrayList<QuizQuestion>
    private var isSuccess = true
    private var isQuiz by Delegates.notNull<Boolean>()
    private var level by Delegates.notNull<Int>()
    private var threshold by Delegates.notNull<Float>()

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

        info = intent.getParcelableExtra(INFO)!!

        quizzes = info.quizzes
        isQuiz = info.isQuiz
        level = info.level

        threshold = if (level == 1) {
            0.92f
        } else {
            0.98f
        }

        binding.skipQuizButton.setOnClickListener {
            if (isQuiz) {
                showSkipDialog { skipQuiz() }
            } else {
                skipQuiz()
            }
        }

        binding.skipQuestionButton.setOnClickListener {
            if (isQuiz) {
                showSkipDialog { skipQuestion() }
            } else {
                skipQuestion()
            }
        }

        binding.helpButton.setOnClickListener {
            if (isQuiz) {
                showQuizHelpDialog()
            } else {
                showExerciseHelpDialog()
            }
        }

        setupUI()
    }

    private fun setupUI() {
        supportActionBar?.hide()

        binding.skipQuizButton.text = if (isQuiz) {
            "Lewati Kuis"
        } else {
            "Lewati latihan"
        }

        binding.textThreshold.text =
            getString(R.string.acceptance_threshold, threshold.times(100).toString())

        binding.textQuestionSwitcher.setInAnimation(
            this,
            androidx.appcompat.R.anim.abc_slide_in_bottom
        )
        binding.textQuestionSwitcher.setOutAnimation(
            this,
            androidx.appcompat.R.anim.abc_slide_out_top
        )

        if (level == 1 && isQuiz) {
            showQuizHelpDialog()
        }

        if (level == 1 && !isQuiz) {
            showExerciseHelpDialog()
        }

        showQuestion()
        if (!isQuiz) {
            showImage()
        }
    }

    private fun showQuestion() {
        binding.textQuestionSwitcher.setText(quizzes[0].question?.uppercase())
    }

    private fun showImage() {
        binding.draggableCard1.visibility = View.VISIBLE
        binding.imageReference.visibility = View.VISIBLE
        Glide.with(this).load(quizzes[0].image)
            .transition(DrawableTransitionOptions.withCrossFade()).into(binding.imageReference)
    }

    private fun showQuizHelpDialog() {
        val text =
            "Jika berhasil menjawab semua pertanyaan, kamu akan mendapatkan EXP \n\nNamun, jika gagal atau melewati kamu tidak akan mendapatkan EXP"
        showHelpDialog(this, "PETUNJUK", text)
    }

    private fun showExerciseHelpDialog() {
        val text =
            "Mode latihan dilengkapi dengan referensi gambar untuk memudahkan kamu berlatih!\n\nGambar dapat dipindahkan sesukamu dengan menggeser gambar di layar\n\nKamu tidak dapat mendapatkan EXP di mode ini"
        showHelpDialog(this, "PETUNJUK", text)
    }

    private fun showSkipDialog(skipFun: () -> Unit) {
        val builder: AlertDialog.Builder =
            AlertDialog.Builder(this)

        builder.setMessage("Apakah kamu yakin ingin melewati? Hasil akan dianggap gagal")
            ?.setPositiveButton(
                R.string.dialog_message_yes
            ) { dialog, _ ->
                dialog.dismiss()
                skipFun()
            }?.setNegativeButton(R.string.dialog_message_no) { dialog, _ ->
                dialog.cancel()
            }

        val dialog: AlertDialog? = builder.create()
        dialog?.show()
    }

    private fun skipQuestion() {
        if (quizzes.isNotEmpty()) {
            isSuccess = false
            advanceQuestion()
        }
    }

    private fun skipQuiz() {
        isSuccess = false
        exitQuiz()
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

                val modelPath = when (level) {
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

                reportPrediction(resultToDisplay)
                resultToDisplay?.let { checkAnswer(it) }
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

            Log.d(TAG, "Detected object: $i ")
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
            if (quizzes.isNotEmpty()) {
                if (result.predictionText.equals(
                        quizzes[0].question,
                        true
                    ) && result.score >= threshold
                ) {
                    Log.d("RESULT", "BENARRRR")
                    showToast(this, "Benar! ${quizzes[0].question} terdeteksi")
                    advanceQuestion()
                }
            }
        }
    }

    private fun advanceQuestion() {
        if (quizzes.isNotEmpty()) {
            quizzes.removeAt(0)
            if (quizzes.isNotEmpty()) {
                showQuestion()
                if (!isQuiz) {
                    showImage()
                }
            } else {
                checkQuizState()
            }
        }
    }

    private fun checkQuizState() {
        if (quizzes.isEmpty()) {
            binding.textQuestionSwitcher.setText("Selamat! Anda berhasil melewati kuis ini")

            exitQuiz()
        }
    }

    private fun exitQuiz() {
        val intent = Intent(this, FinishActivity::class.java)
        intent.putExtra(FinishActivity.IS_SUCCESS, isSuccess)
        intent.putExtra(FinishActivity.IS_QUIZ, isQuiz)
        intent.putExtra(FinishActivity.FROM_LEVEL, level)
        startActivity(intent)
        finish()
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
        val convertedLocation = RectF(
            location.left * 0.01f,
            location.top * 0.01f,
            location.right * 0.01f,
            location.bottom * 0.01f
        )

        Log.d("LOCATION", convertedLocation.toString())
        // Step 1: map location to the preview coordinates
        val previewLocation = RectF(
            location.left * binding.viewFinder.width,
            location.top * binding.viewFinder.height,
            location.right * binding.viewFinder.width,
            location.bottom * binding.viewFinder.height
        )
        Log.d("LOCATION", previewLocation.toString())

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

        const val INFO = "info"
    }
}


