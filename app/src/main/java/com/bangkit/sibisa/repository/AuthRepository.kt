package com.bangkit.sibisa.repository

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import com.bangkit.sibisa.models.ErrorResponse
import com.bangkit.sibisa.models.login.LoginRequest
import com.bangkit.sibisa.models.register.RegisterRequest
import com.bangkit.sibisa.models.result.NetworkResult
import com.bangkit.sibisa.retrofit.RetrofitService
import com.google.gson.Gson
import retrofit2.HttpException

class AuthRepository(private val retrofitService: RetrofitService) {
    fun makeLoginRequest(loginData: LoginRequest): LiveData<NetworkResult<String?>> = liveData {
        emit(NetworkResult.Loading)
        try {
            val response = retrofitService.login(loginData)

            if (response.status in 599 downTo 400) {
                Log.d("ERROR", response.toString())
                throw Exception(response.message)
            }

            val token = response.data?.token

            emit(NetworkResult.Success(token))
        } catch (e: Exception) {
            val errorBody = Gson().fromJson(
                (e as? HttpException)?.response()?.errorBody()
                    ?.charStream(), ErrorResponse::class.java
            )
            emit(NetworkResult.Error(errorBody.errorCode.toString()))
        }
    }

    fun makeRegisterRequest(registerData: RegisterRequest): LiveData<NetworkResult<Boolean>> =
        liveData {
            emit(NetworkResult.Loading)
            try {
                val response = retrofitService.register(registerData)

                if (response.status in 599 downTo 400) {
                    throw Exception(response.message)
                }

                emit(NetworkResult.Success(true))
            } catch (e: Exception) {
                val errorBody = Gson().fromJson(
                    (e as? HttpException)?.response()?.errorBody()
                        ?.charStream(), ErrorResponse::class.java
                )
                emit(NetworkResult.Error(errorBody.errorCode.toString()))
            }
        }
}