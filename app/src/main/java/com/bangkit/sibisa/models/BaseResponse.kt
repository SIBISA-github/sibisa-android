package com.bangkit.sibisa.models

import com.google.gson.annotations.SerializedName

data class BaseResponse<T>(
    @field:SerializedName("status")
    val status: Int = 400,

    @field:SerializedName("message")
    val message: String = "default",

    @field:SerializedName("data")
    val data: T? = null
)