package com.bangkit.sibisa.models

import com.google.gson.annotations.SerializedName

data class ErrorResponse(

	@field:SerializedName("error_code")
	val errorCode: String? = null,

	@field:SerializedName("message")
	val message: Int? = null,

	@field:SerializedName("status")
	val status: Boolean? = null
)
