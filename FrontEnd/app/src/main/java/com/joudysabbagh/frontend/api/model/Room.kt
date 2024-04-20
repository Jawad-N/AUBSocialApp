package com.joudysabbagh.frontend.api.model

import com.google.gson.annotations.SerializedName

class Room {
    @SerializedName("building")
    var building: String? = null

    @SerializedName("day")
    var day: String? = null

    @SerializedName("start_time")
    var start_time: String? = null

    @SerializedName("end_time")
    var end_time: String? = null

    @SerializedName("room")
    var room: String? = null
}
