package com.joudysabbagh.frontend.api.model

import com.google.gson.annotations.SerializedName
class Email {
    @SerializedName("code")
    var code: String? = null
    @SerializedName("email")
    var email: String? = null
    @SerializedName("new_password")
    var password: String? = null
}