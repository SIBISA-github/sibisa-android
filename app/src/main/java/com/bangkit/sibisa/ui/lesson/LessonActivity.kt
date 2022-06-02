package com.bangkit.sibisa.ui.lesson

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import com.bangkit.sibisa.R
import com.bangkit.sibisa.databinding.ActivityLessonBinding
import com.bangkit.sibisa.factory.ViewModelFactory
import com.bangkit.sibisa.ui.leaderboard.LeaderboardViewModel
import com.bangkit.sibisa.ui.login.LoginViewModel

class LessonActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLessonBinding
    private val level = intent.getIntExtra(LEVEL, 1)
    private lateinit var viewModel: QuizViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLessonBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel = ViewModelProvider(this, ViewModelFactory(this))[QuizViewModel::class.java]
    }

    private fun setupUI() {

    }

    companion object {
        const val LEVEL = "level"
    }
}