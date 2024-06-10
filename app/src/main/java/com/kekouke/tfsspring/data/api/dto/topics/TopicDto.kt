package com.kekouke.tfsspring.data.api.dto.topics

import com.google.gson.annotations.SerializedName

class TopicDto(
    @SerializedName("name") val name: String,
)