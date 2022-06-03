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
import com.bangkit.sibisa.models.result.NetworkResult
import com.bangkit.sibisa.ui.quiz.QuizActivity
import com.bangkit.sibisa.utils.showToast
import com.yuyakaido.android.cardstackview.*
import okhttp3.internal.wait

class LessonActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLessonBinding
    private lateinit var viewModel: QuizViewModel
    private lateinit var lessons: List<Lesson?>
    private lateinit var questions: List<Question?>
    private lateinit var cardStackView: CardStackView

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

        binding.lessonButton.setOnClickListener {
            goToQuiz()
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
        val level = intent.getIntExtra(LEVEL, 1)
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

    private fun goToQuiz() {
        val level = intent.getIntExtra(LEVEL, 1)
        viewModel.getQuestionsByLevel(level).observe(this) { result ->
            if (result != null) {
                when (result) {
                    is NetworkResult.Loading -> {
                        binding.progressBar.visibility = View.VISIBLE
                        binding.progressBar.bringToFront()
                    }
                    is NetworkResult.Success -> {
                        binding.progressBar.visibility = View.GONE

                        if (!result.data.isNullOrEmpty()) {
                            questions = result.data

                            val mappedQuestions = ArrayList(questions.map {
                                it?.question
                            })

                            Log.d("QUESTIONS", mappedQuestions.toString())

                            val intent = Intent(this, QuizActivity::class.java)
                            intent.putExtra(QuizActivity.LEVEL, LEVEL)
                            intent.putStringArrayListExtra(QuizActivity.QUESTIONS, mappedQuestions)
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