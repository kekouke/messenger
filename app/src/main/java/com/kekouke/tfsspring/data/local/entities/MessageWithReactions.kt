package com.kekouke.tfsspring.data.local.entities

import androidx.room.Embedded
import androidx.room.Relation

class MessageWithReactions(
    @Embedded val message: MessageEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "messageId"
    )
    val reactions: List<ReactionEntity>
)