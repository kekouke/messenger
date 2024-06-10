package com.kekouke.tfsspring.domain.model

sealed interface PollEvent {

    sealed class Chat : PollEvent {
        data class MessageEvent(val message: Message) : Chat()
        data class ReactionEvent(
            val messageId: Int,
            val reaction: Reaction,
            val operation: ReactionOperationType,
            val isMeReaction: Boolean
        ) : Chat()
    }

    sealed class Streams : PollEvent

}