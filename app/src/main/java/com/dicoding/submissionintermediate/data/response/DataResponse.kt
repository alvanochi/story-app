package com.dicoding.submissionintermediate.data.response

import com.google.gson.annotations.SerializedName

data class DataResponse(
    @field:SerializedName("error")
    val error: Boolean? = null,

    @field:SerializedName("message")
    val message: String? = null
)