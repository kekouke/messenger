package com.kekouke.tfsspring.data.mappers

import com.kekouke.tfsspring.data.api.dto.topics.TopicDto
import com.kekouke.tfsspring.data.local.entities.TopicEntity
import com.kekouke.tfsspring.domain.model.Stream
import com.kekouke.tfsspring.domain.model.Topic

fun TopicEntity.toDomain() = Topic(
    name = name,
    streamId = streamId,
    streamName = streamName
)

fun TopicDto.toDomain(stream: Stream) = Topic(
    name = name,
    streamId = stream.id,
    streamName = stream.name
)

fun TopicDto.toEntity(stream: Stream) = TopicEntity(
    name = name,
    streamId = stream.id,
    streamName = stream.name
)