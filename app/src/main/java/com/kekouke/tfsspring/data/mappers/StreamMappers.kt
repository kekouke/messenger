package com.kekouke.tfsspring.data.mappers

import com.kekouke.tfsspring.data.api.dto.streams.StreamDto
import com.kekouke.tfsspring.data.local.entities.StreamEntity
import com.kekouke.tfsspring.domain.model.Stream

fun StreamEntity.toDomain() = Stream(
    id = id,
    name = name
)

fun StreamDto.toEntity(subscribed: Boolean) = StreamEntity(
    id = id,
    name = name,
    subscribed = subscribed
)