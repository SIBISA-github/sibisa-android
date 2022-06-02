package com.bangkit.sibisa.ui.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.bangkit.sibisa.models.login.LoginRequest
import com.bangkit.sibisa.models.result.NetworkResult
import com.bangkit.sibisa.repository.AuthRepository

class LoginViewModel(private val authRepository: AuthRepository) : ViewModel() {
    fun login(username: String, password: String): LiveData<NetworkResult<String?>> {
        val body = LoginRequest(username = username, password = password)
        return authRepository.makeLoginRequest(body)
    }
}