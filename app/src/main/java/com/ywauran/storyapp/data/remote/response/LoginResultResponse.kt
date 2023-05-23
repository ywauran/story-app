package com.ywauran.storyapp.data.remote.response

import com.google.gson.annotations.SerializedName

data class LoginResultResponse (
    @SerializedName("name")
    var name: String,
    @SerializedName("token")
    var token: String,
    @SerializedName("userId")
    var userId: String
    )