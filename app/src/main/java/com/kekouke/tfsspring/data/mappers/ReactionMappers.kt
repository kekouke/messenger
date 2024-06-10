package com.kekouke.tfsspring.data.mappers

import com.kekouke.tfsspring.data.api.MY_ID
import com.kekouke.tfsspring.data.api.dto.messages.ReactionDto
import com.kekouke.tfsspring.data.local.entities.ReactionEntity
import com.kekouke.tfsspring.domain.model.Reaction

private const val UNICODE_EMOJI = "unicode_emoji"
private const val RADIX = 16

fun List<ReactionDto>.dtoToDomainList(): List<Reaction> =
    filter { it.reactionType == UNICODE_EMOJI }
        .groupBy { it.emojiCode }
        .map { (emojiCode, reactions) ->

            val decodedEmojiCode = decode(emojiCode)

            Reaction(
                reactions.first().emojiName,
                decodedEmojiCode,
                reactions.size,
                reactions.any { it.userId == MY_ID }
            )
        }

fun List<ReactionEntity>.entityToDomainList(): List<Reaction> = map {
    Reaction(
        it.name,
        it.code,
        it.count,
        it.selected
    )
}

fun List<Reaction>.toEntityList(messageId: Int): List<ReactionEntity> = map { reaction ->
    ReactionEntity(
        messageId,
        reaction.code,
        reaction.name,
        reaction.count,
        reaction.selected
    )
}

fun decode(code: String): String = code.split('-')
    .joinToString(separator = "") {
        String(Character.toChars(it.toInt(RADIX)))
    }