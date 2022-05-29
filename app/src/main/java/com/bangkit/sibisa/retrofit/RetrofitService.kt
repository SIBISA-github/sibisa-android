package com.bangkit.sibisa.retrofit

import com.bangkit.sibisa.models.BaseResponse
import com.bangkit.sibisa.models.login.LoginRequest
import com.bangkit.sibisa.models.login.LoginResponse
import com.bangkit.sibisa.models.profile.Profile
import com.bangkit.sibisa.models.register.RegisterRequest
import retrofit2.http.*

interface RetrofitService {
    @Headers("Content-Type: application/json", "No-Authentication: true")
    @POST("user/register")
    suspend fun register(
        @Body registerData: RegisterRequest
    ): BaseResponse<Nothing>

    @Headers("Content-Type: application/json", "No-Authentication: true")
    @POST("user/login")
    suspend fun login(
        @Body loginData: LoginRequest
    ): BaseResponse<LoginResponse>

    @GET("user/{id}")
    suspend fun getUserProfileById(
        @Path ("id") id: Int
    ): BaseResponse<Profile>
}