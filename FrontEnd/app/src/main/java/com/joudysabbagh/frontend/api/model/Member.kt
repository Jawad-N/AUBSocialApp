package com.joudysabbagh.frontend.api.model

import com.google.gson.annotations.SerializedName

class Member {
    @SerializedName("group")
    var group: String? = null
    @SerializedName("member")
    var member: String? = null
}