package com.kekouke.tfsspring.data.mappers

import com.kekouke.tfsspring.data.api.MY_ID
import com.kekouke.tfsspring.data.api.dto.messages.MessageDto
import com.kekouke.tfsspring.data.local.entities.MessageEntity
import com.kekouke.tfsspring.data.local.entities.MessageWithReactions
import com.kekouke.tfsspring.domain.model.Message
import com.kekouke.tfsspring.domain.model.Topic

private const val MILLISECONDS_PER_SECOND = 1000

fun MessageDto.toDomain() = Message(
    id = id,
    content = content,
    userAvatarUrl = senderAvatarUrl,
    username = senderFullName,
    isReceivedMessage = senderId != MY_ID,
    senderId = senderId,
    timestampMilliseconds = timestamp * MILLISECONDS_PER_SECOND,
    reactions = reactions.dtoToDomainList()
)

fun MessageWithReactions.toDomain() = Message(
    id = message.id,
    content = message.content,
    userAvatarUrl = message.senderAvatarUrl,
    username = message.senderFullName,
    isReceivedMessage = message.senderId != MY_ID,
    senderId = message.senderId,
    timestampMilliseconds = message.timestampMilliseconds,
    reactions = reactions.entityToDomainList()
)

fun Message.toEntity(topic: Topic) = MessageEntity(
    id = id,
    content = content,
    senderAvatarUrl = userAvatarUrl,
    senderFullName = username,
    senderId = senderId,
    streamId = topic.streamId,
    topicName = topic.name,
    timestampMilliseconds = timestampMilliseconds,
)