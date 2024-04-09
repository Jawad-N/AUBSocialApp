package com.joudysabbagh.frontend.api.model

import com.google.gson.annotations.SerializedName
class User {
    @SerializedName("email")
    var email: String? = null
    @SerializedName("username")
    var username: String? = null
    @SerializedName("password")
    var password: String? = null
}