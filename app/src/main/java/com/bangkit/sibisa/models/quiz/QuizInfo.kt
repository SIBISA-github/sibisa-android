package com.bangkit.sibisa.models.quiz

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class QuizInfo(
    val level: Int,
    val quizzes: ArrayList<QuizQuestion>,
    val isQuiz: Boolean
) : Parcelable

@Parcelize
data class QuizQuestion(
    val question: String?,
    val image: String?,
) : Parcelable
