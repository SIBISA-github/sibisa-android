package com.bangkit.sibisa.ui.profile

import android.content.Intent
import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.bangkit.sibisa.models.profile.Profile
import com.bangkit.sibisa.models.result.NetworkResult
import com.bangkit.sibisa.pref.UserPreference
import com.bangkit.sibisa.repository.ProfileRepository
import com.bangkit.sibisa.ui.MainActivity
import com.bangkit.sibisa.utils.showToast

class ProfileViewModel(private val profileRepository: ProfileRepository) : ViewModel() {
    fun getUserProfile(userID: Int) = profileRepository.getUserProfile(userID)
}