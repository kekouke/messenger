package com.kekouke.tfsspring.data.api.response.presence

import com.google.gson.annotations.SerializedName

class Presence(
    @SerializedName("aggregated") val detail: PresenceDetail
)