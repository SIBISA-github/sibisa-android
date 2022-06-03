package com.bangkit.sibisa.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import com.bangkit.sibisa.models.ErrorResponse
import com.bangkit.sibisa.models.lesson.Lesson
import com.bangkit.sibisa.models.question.Question
import com.bangkit.sibisa.models.result.NetworkResult
import com.bangkit.sibisa.retrofit.RetrofitService
import com.google.gson.Gson
import retrofit2.HttpException

class QuizRepository(private val retrofitService: RetrofitService) {
    fun getLessonsByLevel(level: Int): LiveData<NetworkResult<List<Lesson?>?>> = liveData {
        emit(NetworkResult.Loading)
        try {
            val response = retrofitService.getLessonsByLevel(level)

            if (response.status in 599 downTo 400) {
                throw Exception(response.message)
            }

            val data = response.data

            emit(NetworkResult.Success(data))
        } catch (e: Exception) {
            val errorBody = Gson().fromJson(
                (e as? HttpException)?.response()?.errorBody()
                    ?.charStream(), ErrorResponse::class.java
            ) ?: null
            emit(NetworkResult.Error(errorBody?.errorCode.toString()))
        }
    }

    fun getQuestionsByLevel(level: Int): LiveData<NetworkResult<List<Question?>?>> = liveData {
        emit(NetworkResult.Loading)
        try {
            val response = retrofitService.getQuestionsByLevel(level)

            if (response.status in 599 downTo 400) {
                throw Exception(response.message)
            }

            val data = response.data

            emit(NetworkResult.Success(data))
        } catch (e: Exception) {
            val errorBody = Gson().fromJson(
                (e as? HttpException)?.response()?.errorBody()
                    ?.charStream(), ErrorResponse::class.java
            ) ?: null
            emit(NetworkResult.Error(errorBody?.errorCode.toString()))
        }
    }
}