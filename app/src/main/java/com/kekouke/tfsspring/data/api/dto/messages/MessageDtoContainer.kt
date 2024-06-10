package com.kekouke.tfsspring.data.api.dto.messages

import com.google.gson.annotations.SerializedName

class MessageDtoContainer(
    @SerializedName("messages") val messages: List<MessageDto>,
    @SerializedName("found_oldest") val foundOldest: Boolean
)