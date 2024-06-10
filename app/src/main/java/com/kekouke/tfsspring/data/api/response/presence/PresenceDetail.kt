package com.kekouke.tfsspring.data.api.response.presence

import com.google.gson.annotations.SerializedName

class PresenceDetail(
    @SerializedName("status") val status: String,
    @SerializedName("timestamp") val timestamp: Long
)