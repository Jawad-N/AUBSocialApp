package com.joudysabbagh.frontend.api.model
import com.google.gson.annotations.SerializedName

class StudyGroup(
    @SerializedName("id") val id: Int?,
    @SerializedName("name") val name: String,
    @SerializedName("description") val description: String?,
    @SerializedName("members") val members: List<String> // Assuming members are represented as a list of strings (usernames or IDs)
) {
    constructor(name: String) : this(null, name, null, emptyList())
}
