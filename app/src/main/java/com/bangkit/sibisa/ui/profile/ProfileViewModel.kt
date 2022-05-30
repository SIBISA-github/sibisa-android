package com.bangkit.sibisa.ui.profile

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.bangkit.sibisa.models.profile.UpdateProfileResponse
import com.bangkit.sibisa.models.result.NetworkResult
import com.bangkit.sibisa.repository.ProfileRepository
import com.bangkit.sibisa.utils.reduceFileImage
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File

class ProfileViewModel(private val profileRepository: ProfileRepository) : ViewModel() {
    fun getUserProfile(userID: Int) = profileRepository.getUserProfile(userID)

    fun updateProfile(
        userID: Int,
        getFile: File?
    ): LiveData<NetworkResult<UpdateProfileResponse>>? {
        val file = getFile?.let { reduceFileImage(it) }
        val requestImageFile = file?.asRequestBody("image/jpeg".toMediaTypeOrNull())
        val imageMultipart: MultipartBody.Part? = requestImageFile?.let {
            MultipartBody.Part.createFormData(
                "image", file.name, it
            )
        }

        return imageMultipart?.let {
            profileRepository.updateUserProfile(
                userID,
                it
            )
        }
    }
}