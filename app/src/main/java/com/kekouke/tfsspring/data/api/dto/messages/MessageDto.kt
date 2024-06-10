package com.kekouke.tfsspring.data.api.dto.messages

import com.google.gson.annotations.SerializedName

class MessageDto(
    @SerializedName("id") val id: Int,
    @SerializedName("content") val content: String,
    @SerializedName("avatar_url") val senderAvatarUrl: String,
    @SerializedName("sender_full_name") val senderFullName: String,
    @SerializedName("sender_id") val senderId: Int,
    @SerializedName("stream_id") val streamId: Int,
    @SerializedName("subject") val topicName: String,
    @SerializedName("timestamp") val timestamp: Long,
    @SerializedName("reactions") val reactions: List<ReactionDto>,
)