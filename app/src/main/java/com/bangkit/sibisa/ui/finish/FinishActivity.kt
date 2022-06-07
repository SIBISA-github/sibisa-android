package com.bangkit.sibisa.ui.finish

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Intent
import android.graphics.drawable.ColorDrawable
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.core.content.res.ResourcesCompat
import androidx.lifecycle.ViewModelProvider
import com.bangkit.sibisa.R
import com.bangkit.sibisa.databinding.ActivityFinishBinding
import com.bangkit.sibisa.factory.ViewModelFactory
import com.bangkit.sibisa.models.result.NetworkResult
import com.bangkit.sibisa.ui.MainActivity
import com.bangkit.sibisa.ui.lesson.LessonActivity
import com.bangkit.sibisa.utils.showToast
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import kotlin.properties.Delegates

class FinishActivity : AppCompatActivity() {
    private var isSuccess by Delegates.notNull<Boolean>()
    private var isQuiz by Delegates.notNull<Boolean>()
    private var fromLevel by Delegates.notNull<Int>()

    private lateinit var binding: ActivityFinishBinding
    private lateinit var viewModel: FinishViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityFinishBinding.inflate(layoutInflater)
        setContentView(binding.root)

        isSuccess = intent.getBooleanExtra(IS_SUCCESS, true)
        isQuiz = intent.getBooleanExtra(IS_QUIZ, true)
        fromLevel = intent.getIntExtra(FROM_LEVEL, 1)

        viewModel = ViewModelProvider(this, ViewModelFactory(this))[FinishViewModel::class.java]

        setupUI()
        setupAction()
    }

    private fun setupUI() {
        setupActionBar()
        if (isSuccess) {
            if (isQuiz) {
                updateLevel()
            } else {
                displayCongrats()
            }

            Glide.with(this).load(
                ResourcesCompat.getDrawable(
                    resources,
                    R.drawable.trophy_2,
                    null
                )
            )
                .transition(DrawableTransitionOptions.withCrossFade())
                .into(binding.finishImage)
        } else {
            binding.finishTextHeading.text = getString(R.string.failed_text_heading)
            binding.finishText.text = getString(R.string.failed_text)

            Glide.with(this).load(
                ResourcesCompat.getDrawable(
                    resources,
                    R.drawable.ic_cross,
                    null
                )
            )
                .transition(DrawableTransitionOptions.withCrossFade())
                .into(binding.finishImage)

            if (isQuiz) {
                binding.expText.text = getString(R.string.text_exp, "0")
            }

            playLevelAnimation()
            playExpAnimation()
        }
    }

    private fun setupAction() {
        binding.finishButton.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }

        binding.backToLessonButton.setOnClickListener {
            val intent = Intent(this, LessonActivity::class.java)
            intent.putExtra(LessonActivity.LEVEL, fromLevel)
            startActivity(intent)
            finish()
        }
    }

    private fun setupActionBar() {
        supportActionBar?.let {
            it.setDisplayShowTitleEnabled(false)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                it.setBackgroundDrawable(ColorDrawable(getColor(R.color.riviera_paradise)))
            }

            // logo for action bar
            it.setDisplayShowCustomEnabled(true)
            val view = layoutInflater.inflate(R.layout.custom_image, null)
            it.customView = view
        }
    }

    private fun updateLevel() {
        viewModel.updateLevel(fromLevel+1).observe(this) { result ->
            if (result != null) {
                when (result) {
                    is NetworkResult.Loading -> {
                        binding.progressBar.visibility = View.VISIBLE
                        binding.progressBar.bringToFront()
                    }
                    is NetworkResult.Success -> {
                        displayCongrats()

                        if (isQuiz) {
                            updateExp()
                        }
                    }
                    is NetworkResult.Error -> {
                        binding.progressBar.visibility = View.GONE
                        showToast(this, result.error.uppercase())
                    }
                }
            }
        }
    }

    private fun updateExp() {
        val exp = fromLevel * 100
        viewModel.updateExp(exp).observe(this) { result ->
            if (result != null) {
                when (result) {
                    is NetworkResult.Loading -> {
                        binding.progressBar2.visibility = View.VISIBLE
                        binding.progressBar2.bringToFront()
                    }
                    is NetworkResult.Success -> {
                        displayExp()
                    }
                    is NetworkResult.Error -> {
                        binding.progressBar2.visibility = View.GONE
                        showToast(this, result.error.uppercase())
                    }
                }
            }
        }
    }

    private fun displayCongrats() {
        binding.progressBar.visibility = View.GONE

        binding.finishTextHeading.text = getString(R.string.congrats_text_heading)
        binding.finishText.text = getString(R.string.congrats_text)

        playLevelAnimation()
    }

    private fun displayExp() {
        val exp = fromLevel * 100
        binding.progressBar2.visibility = View.GONE

        binding.expText.text = getString(R.string.text_exp, exp.toString())

        playExpAnimation()
    }

    private fun playLevelAnimation() {
        val heading =
            ObjectAnimator.ofFloat(binding.finishTextHeading, View.ALPHA, 1f).setDuration(1000)
        val text = ObjectAnimator.ofFloat(binding.finishText, View.ALPHA, 1f).setDuration(1000)

        AnimatorSet().apply {
            playTogether(heading, text)
            start()
        }
    }

    private fun playExpAnimation() {
        val exp =
            ObjectAnimator.ofFloat(binding.expText, View.ALPHA, 1f).setDuration(1000)

        AnimatorSet().apply {
            play(exp)
            start()
        }
    }

    companion object {
        const val IS_SUCCESS = "success"
        const val IS_QUIZ = "quiz"
        const val FROM_LEVEL = "0"
    }
}