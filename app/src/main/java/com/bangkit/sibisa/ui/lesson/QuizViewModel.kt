package com.bangkit.sibisa.ui.lesson

import androidx.lifecycle.ViewModel
import com.bangkit.sibisa.repository.QuizRepository

class QuizViewModel(private val quizRepository: QuizRepository) : ViewModel() {
    fun getLessonsByLevel(level: Int) = quizRepository.getLessonsByLevel(level)

    fun getQuestionsByLevel(level: Int) = quizRepository.getQuestionsByLevel(level)
}