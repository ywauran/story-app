package com.ywauran.storyapp.data.remote.response

import com.google.gson.annotations.SerializedName

data class LoginResponse (
    @SerializedName("error")
    var error: Boolean,
    @SerializedName("loginResult")
    var loginResult: LoginResultResponse? = null,
    @SerializedName("message")
    var message: String
)