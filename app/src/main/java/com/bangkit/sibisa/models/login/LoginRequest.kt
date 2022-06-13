package com.bangkit.sibisa.models.login

import com.google.gson.annotations.SerializedName

data class LoginRequest(

    @field:SerializedName("password")
    val password: String? = null,

    @field:SerializedName("username")
    val username: String? = null
)
