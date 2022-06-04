package com.bangkit.sibisa.models.exp

import com.google.gson.annotations.SerializedName

data class UpdateExpRequest(
    @field:SerializedName("idLevel")
    val exp: Int? = null
)