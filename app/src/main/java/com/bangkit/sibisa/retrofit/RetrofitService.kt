package com.bangkit.sibisa.retrofit

import com.bangkit.sibisa.models.BaseResponse
import com.bangkit.sibisa.models.exp.UpdateExpRequest
import com.bangkit.sibisa.models.lesson.Lesson
import com.bangkit.sibisa.models.level.UpdateLevelRequest
import com.bangkit.sibisa.models.login.LoginRequest
import com.bangkit.sibisa.models.login.LoginResponse
import com.bangkit.sibisa.models.profile.Profile
import com.bangkit.sibisa.models.profile.UpdateProfileResponse
import com.bangkit.sibisa.models.question.Question
import com.bangkit.sibisa.models.register.RegisterRequest
import okhttp3.MultipartBody
import okhttp3.RequestBody
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
        @Path("id") id: Int
    ): BaseResponse<Profile>

    @GET("user")
    suspend fun getAllUserProfiles(): BaseResponse<List<Profile?>>

    @Multipart
    @PUT("user/{id}")
    suspend fun updateProfile(
        @Path("id") id: Int,
        @Part image: MultipartBody.Part,
        @Part("name") name: RequestBody?,
        @Part("username") username: RequestBody?,
    ): BaseResponse<UpdateProfileResponse>

    @GET("lessons/level/{level}")
    suspend fun getLessonsByLevel(
        @Path("level") level: Int
    ): BaseResponse<List<Lesson?>>

    @GET("questions/level/{level}")
    suspend fun getQuestionsByLevel(
        @Path("level") level: Int
    ): BaseResponse<List<Question?>>

    @PUT("user/level")
    suspend fun updateLevel(
        @Body level: UpdateLevelRequest
    ): BaseResponse<Nothing>

    @PUT("user/exp")
    suspend fun updateExp(
        @Body expData: UpdateExpRequest
    ): BaseResponse<Nothing>
}