package com.joudysabbagh.frontend.api.model
import com.google.gson.annotations.SerializedName

class StudyGroup {
    @SerializedName("creator")
    var creator: String? = null

    @SerializedName("group_name")
    var name: String? = null

    @SerializedName("description")
    var description: String? = null

    //@SerializedName("members")
    //val members: List<String> // Assuming members are represented as a list of strings (usernames or IDs)
}
