package com.joudysabbagh.frontend.api.model

import com.google.gson.annotations.SerializedName

class TutoringSession {
    @SerializedName("courseID")
    var courseID: String? = null

    @SerializedName("course_name")
    var course_name: String? = null

    @SerializedName("description")
    var description: String? = null

    @SerializedName("price")
    var price: Float? = null
}