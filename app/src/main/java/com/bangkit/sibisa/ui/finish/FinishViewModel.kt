package com.bangkit.sibisa.ui.finish

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.bangkit.sibisa.models.exp.UpdateExpRequest
import com.bangkit.sibisa.models.level.UpdateLevelRequest
import com.bangkit.sibisa.models.profile.Profile
import com.bangkit.sibisa.models.result.NetworkResult
import com.bangkit.sibisa.repository.ProfileRepository

class FinishViewModel(private val profileRepository: ProfileRepository) : ViewModel() {
    fun updateExp(exp: Int): LiveData<NetworkResult<Profile>> {
        val body = UpdateExpRequest(exp)
        return profileRepository.updateExp(body)
    }

    fun updateLevel(level: Int): LiveData<NetworkResult<Profile>> {
        val body = UpdateLevelRequest(level)
        return profileRepository.updateLevel(body)
    }
}