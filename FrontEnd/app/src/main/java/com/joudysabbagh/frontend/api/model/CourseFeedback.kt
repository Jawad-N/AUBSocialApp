package com.joudysabbagh.frontend.api.model

import com.google.gson.annotations.SerializedName

class CourseFeedback {
    @SerializedName("course_name")
    var course_name: String? = null

    @SerializedName("section")
    var section: String? = null

    @SerializedName("professor")
    var professor: String? = null

    @SerializedName("content")
    var content: String? = null

}