package com.ywauran.storyapp.data.remote.response

import com.google.gson.annotations.SerializedName

data class UserResponse(
    @field:SerializedName("name")
    var name: String? = null,

    @field:SerializedName("email")
    var email: String? = null,

    @field:SerializedName("password")
    var password: String? = null,
)
