package com.bangkit.sibisa.models

data class BaseResponse<T>(
    val status: Int? = null,
    val message: String? = null,
    val data: T? = null
)