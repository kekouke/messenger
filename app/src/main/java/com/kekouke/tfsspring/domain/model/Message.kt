package com.kekouke.tfsspring.domain.model

data class Message(
    val content: String,
    val isReceivedMessage: Boolean,
    val senderId: Int,
    val timestampMilliseconds: Long,
    val userAvatarUrl: String = "",
    val username: String = "",
    val reactions: List<Reaction> = emptyList(),
    val id: Int = -1,
)