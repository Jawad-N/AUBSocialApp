package com.joudysabbagh.frontend.api.model

import com.google.gson.annotations.SerializedName

class CourseFeedback {
    @SerializedName("course_name")
    var course_name: String? = null

    @SerializedName("course_section")
    var course_section: String? = null

    @SerializedName("professor")
    var professor: String? = null

    @SerializedName("content")
    var content: String? = null

}