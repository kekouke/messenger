package com.kekouke.tfsspring.data.api.dto.topics

import com.google.gson.annotations.SerializedName

class TopicDtoContainer(
    @SerializedName("topics") val topics: List<TopicDto>,
)
