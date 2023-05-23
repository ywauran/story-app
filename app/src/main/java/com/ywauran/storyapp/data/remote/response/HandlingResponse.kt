package com.ywauran.storyapp.data.remote.response

import com.google.gson.annotations.SerializedName

data class HandlingResponse(
    @SerializedName("error")
    var error: Boolean,
    @SerializedName("message")
    var message: String
)
