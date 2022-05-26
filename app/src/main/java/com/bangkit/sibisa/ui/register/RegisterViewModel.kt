package com.bangkit.sibisa.ui.register

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.bangkit.sibisa.models.result.NetworkResult
import com.bangkit.sibisa.models.register.RegisterRequest
import com.bangkit.sibisa.repository.AuthRepository

class RegisterViewModel(private val authRepository: AuthRepository): ViewModel() {
    fun register(
        name: String,
        username: String,
        email: String,
        password: String
    ): LiveData<NetworkResult<Boolean>> {
        val body =
            RegisterRequest(username = username, name = name, email = email, password = password)
        return authRepository.makeRegisterRequest(body)
    }
}