package com.bangkit.sibisa.ui.leaderboard

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.bangkit.sibisa.repository.ProfileRepository

class LeaderboardViewModel(private val profileRepository: ProfileRepository) : ViewModel() {
    fun getAllUserProfiles() = profileRepository.getAllUserProfiles()
}