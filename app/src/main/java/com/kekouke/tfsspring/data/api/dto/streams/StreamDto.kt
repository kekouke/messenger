package com.kekouke.tfsspring.data.api.dto.streams

import com.google.gson.annotations.SerializedName

class StreamDto(
    @SerializedName("stream_id") val id: Int,
    @SerializedName("name") val name: String,
    @SerializedName("creator_id") val creatorId: Int,
)