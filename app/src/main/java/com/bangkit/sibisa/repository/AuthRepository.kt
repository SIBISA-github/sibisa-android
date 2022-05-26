package com.bangkit.sibisa.repository

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import com.bangkit.sibisa.models.login.LoginRequest
import com.bangkit.sibisa.models.register.RegisterRequest
import com.bangkit.sibisa.models.result.NetworkResult
import com.bangkit.sibisa.retrofit.RetrofitService

class AuthRepository(private val retrofitService: RetrofitService) {
    fun makeLoginRequest(loginData: LoginRequest): LiveData<NetworkResult<String?>> = liveData {
        emit(NetworkResult.Loading)
        try {
            val response = retrofitService.login(loginData)

            if (response.status in 599 downTo 400) {
                throw Exception(response.message)
            }

            val token = response.data?.token

            emit(NetworkResult.Success(token))
        } catch (e: Exception) {
            emit(NetworkResult.Error(e.message.toString()))
        }
    }

    fun makeRegisterRequest(registerData: RegisterRequest): LiveData<NetworkResult<Boolean>> = liveData {
        emit(NetworkResult.Loading)
        try {
            val response = retrofitService.register(registerData)

            if (response.status in 599 downTo 400) {
                throw Exception(response.message)
            }

            emit(NetworkResult.Success(true))
        } catch (e: Exception) {
            emit(NetworkResult.Error(e.message.toString()))
        }
    }
}