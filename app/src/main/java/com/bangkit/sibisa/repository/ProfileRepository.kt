package com.bangkit.sibisa.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import com.bangkit.sibisa.models.profile.Profile
import com.bangkit.sibisa.models.result.NetworkResult
import com.bangkit.sibisa.retrofit.RetrofitService

class ProfileRepository(private val retrofitService: RetrofitService) {
    fun getAllUserProfiles(): LiveData<NetworkResult<List<Profile?>?>> = liveData {
        emit(NetworkResult.Loading)
        try {
            val response = retrofitService.getAllUserProfiles()

            if (response.status in 599 downTo 400) {
                throw Exception(response.message)
            }

            val data = response.data

            emit(NetworkResult.Success(data))
        } catch (e: Exception) {
            emit(NetworkResult.Error(e.message.toString()))
        }
    }

    fun getUserProfile(userID: Int): LiveData<NetworkResult<Profile>> = liveData {
        emit(NetworkResult.Loading)
        try {
            val response = retrofitService.getUserProfileById(userID)

            if (response.status in 599 downTo 400) {
                throw Exception(response.message)
            }

            val data = response.data!!

            emit(NetworkResult.Success(data))
        } catch (e: Exception) {
            emit(NetworkResult.Error(e.message.toString()))
        }
    }
}