package com.joudysabbagh.frontend.api.model

import com.google.gson.annotations.SerializedName

class TutoringSession {
    @SerializedName("courseID")
    var courseID: String? = null

    @SerializedName("description")
    var description: String? = null

    @SerializedName("price")
    var price: String? = null
}