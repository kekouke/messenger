package com.kekouke.tfsspring.data.api.response.presence

import com.google.gson.annotations.SerializedName

class AllUsersPresenceResponse(
    @SerializedName("server_timestamp") val serverTimestamp: Double,
    @SerializedName("presences")  val presences: Map<String, Presence>
)