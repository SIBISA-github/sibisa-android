package com.bangkit.sibisa.ui.lesson

import android.content.Intent
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.animation.LinearInterpolator
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DefaultItemAnimator
import com.bangkit.sibisa.R
import com.bangkit.sibisa.adapter.LessonCardStackAdapter
import com.bangkit.sibisa.databinding.ActivityLessonBinding
import com.bangkit.sibisa.factory.ViewModelFactory
import com.bangkit.sibisa.models.lesson.Lesson
import com.bangkit.sibisa.models.question.Question
import com.bangkit.sibisa.models.quiz.QuizInfo
import com.bangkit.sibisa.models.quiz.QuizQuestion
import com.bangkit.sibisa.models.result.NetworkResult
import com.bangkit.sibisa.ui.quiz.QuizActivity
import com.bangkit.sibisa.utils.showHelpDialog
import com.bangkit.sibisa.utils.showToast
import com.yuyakaido.android.cardstackview.*
import java.lang.Integer.parseInt
import kotlin.properties.Delegates

class LessonActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLessonBinding
    private lateinit var viewModel: QuizViewModel
    private lateinit var lessons: List<Lesson?>
    private lateinit var questions: List<Question?>
    private lateinit var cardStackView: CardStackView

    private var level by Delegates.notNull<Int>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLessonBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel = ViewModelProvider(this, ViewModelFactory(this))[QuizViewModel::class.java]

        supportActionBar?.let {
            it.setDisplayShowTitleEnabled(true)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                it.setBackgroundDrawable(ColorDrawable(getColor(R.color.riviera_paradise)))
            }

            it.title = "Materi Bahasa Isyarat"
            it.setHomeButtonEnabled(true)
            it.setDisplayHomeAsUpEnabled(true)
        }

        binding.lessonQuizButton.setOnClickListener {
            goToQuestion(true)
        }

        binding.lessonExerciseButton.setOnClickListener {
            goToQuestion(false)
        }

        binding.helpButton.setOnClickListener {
            showHelpDialog(
                this,
                "PETUNJUK",
                "Materi terdiri dari beberapa foto sesuai dengan level yang ada \n\n Kamu dapat melihat foto dengan cara menggeser foto ke arah horizontal \n\n Tekan tombol kuis untuk mengerjakan kuis \n\n Tekan tombol latihan untuk berlatih"
            )
        }

        level = intent.getIntExtra(LEVEL, 0)

        when (level) {
            1 -> binding.lessonHeading.text = resources.getString(R.string.huruf_vokal)
            2 -> binding.lessonHeading.text = resources.getString(R.string.kata_dasar)
            3 -> binding.lessonHeading.text = resources.getString(R.string.kuis_gabungan)
        }
        fetchLessons()
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return super.onSupportNavigateUp()
    }

    private fun setupCards() {
        cardStackView = binding.lessonCarousel
        val adapter = LessonCardStackAdapter(this)
        val manager = CardStackLayoutManager(this, object : CardStackListener {
            override fun onCardDragging(direction: Direction, ratio: Float) {
            }

            override fun onCardSwiped(direction: Direction) {
                adapter.reAddItem(0)
            }

            override fun onCardRewound() {
                Log.d("CARD", "REWIND")
            }

            override fun onCardCanceled() {
            }

            override fun onCardAppeared(view: View, position: Int) {
            }

            override fun onCardDisappeared(view: View, position: Int) {
            }
        })
        manager.setStackFrom(StackFrom.Bottom)
        manager.setVisibleCount(5)
        manager.setTranslationInterval(8.0f)
        manager.setScaleInterval(0.95f)
        manager.setSwipeThreshold(0.3f)
        manager.setMaxDegree(20.0f)
        manager.setDirections(Direction.HORIZONTAL)
        manager.setCanScrollHorizontal(true)
        manager.setSwipeableMethod(SwipeableMethod.Manual)
        manager.setOverlayInterpolator(LinearInterpolator())
        adapter.setLessons(lessons.map {
            it ?: Lesson()
        })
        cardStackView.layoutManager = manager
        cardStackView.adapter = adapter
        cardStackView.itemAnimator = DefaultItemAnimator()
    }

    private fun fetchLessons() {
        viewModel.getLessonsByLevel(level).observe(this) { result ->
            if (result != null) {
                when (result) {
                    is NetworkResult.Loading -> {
                        binding.progressBar.visibility = View.VISIBLE
                        binding.progressBar.bringToFront()
                    }
                    is NetworkResult.Success -> {
                        binding.progressBar.visibility = View.GONE

                        if (!result.data.isNullOrEmpty()) {
                            lessons = result.data
                            setupCards()
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

    private fun goToQuestion(isQuiz: Boolean) {
        viewModel.getQuestionsByLevel(level).observe(this) { result ->
            if (result != null) {
                when (result) {
                    is NetworkResult.Loading -> {
                        binding.progressBar.visibility = View.VISIBLE
                        binding.progressBar.bringToFront()
                    }
                    is NetworkResult.Success -> {
                        binding.progressBar.visibility = View.GONE

                        Log.d("QUIZ_ACTIVITY", result.data.toString())
                        if (!result.data.isNullOrEmpty()) {
                            Log.d("QUIZ_ACTIVITY", "aman")
                            questions = result.data

                            val mappedQuestions = ArrayList(questions.map {
                                QuizQuestion(
                                    it?.question,
                                    it?.image
                                )
                            })

                            Log.d("QUIZ_ACTIVITY", level.toString())
                            val quizInfo = QuizInfo(
                                level = level,
                                isQuiz = isQuiz,
                                quizzes = mappedQuestions
                            )

                            Log.d("QUIZ_ACTIVITY", mappedQuestions.toString())
                            Log.d("QUIZ_ACTIVITY", quizInfo.toString())

                            val intent = Intent(this, QuizActivity::class.java)
                            intent.putExtra(QuizActivity.INFO, quizInfo)
                            startActivity(intent, null)
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

    companion object {
        const val LEVEL = "level"
    }
}