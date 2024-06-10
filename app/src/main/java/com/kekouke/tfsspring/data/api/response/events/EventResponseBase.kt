package com.kekouke.tfsspring.data.api.response.events

import com.google.gson.annotations.SerializedName
import com.kekouke.tfsspring.data.api.dto.messages.MessageDto
import com.kekouke.tfsspring.data.api.dto.streams.StreamDto

sealed class EventResponseBase(
    @SerializedName("id") val id: Int,
    @SerializedName("type") val type: String,
) {
    class UnknownEvent(
        id: Int,
        type: String
    ) : EventResponseBase(id, type)

    class MessageEvent(
        id: Int,
        type: String,
        @SerializedName("message") val message: MessageDto
    ) : EventResponseBase(id, type)

    class ReactionEvent(
        id: Int,
        type: String,
        @SerializedName("emoji_name") val emojiName: String,
        @SerializedName("emoji_code") val emojiCode: String,
        @SerializedName("reaction_type") val reactionType: String,
        @SerializedName("op") val action: String,
        @SerializedName("user_id") val userId: Int,
        @SerializedName("message_id") val messageId: Int
    ) : EventResponseBase(id, type)

    class StreamEvent(
        id: Int,
        type: String,
        @SerializedName("op") val action: String,
        @SerializedName("streams") val streams: List<StreamDto>
    ) : EventResponseBase(id, type)
}