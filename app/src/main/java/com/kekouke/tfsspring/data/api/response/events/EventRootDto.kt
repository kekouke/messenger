package com.kekouke.tfsspring.data.api.response.events

import com.google.gson.annotations.SerializedName

class EventRootDto(
    @SerializedName("events") val events: List<EventResponseBase>
)