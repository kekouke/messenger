package com.kekouke.tfsspring.data.api.response

import com.google.gson.annotations.SerializedName

class RegisterEventQueueResponse(
    @SerializedName("queue_id") val queueId: String,
    @SerializedName("last_event_id") val lastEventId: Int
)