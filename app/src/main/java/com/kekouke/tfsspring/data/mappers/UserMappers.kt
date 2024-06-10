package com.kekouke.tfsspring.data.mappers

import com.kekouke.tfsspring.data.api.dto.users.UserDto
import com.kekouke.tfsspring.data.local.entities.UserEntity
import com.kekouke.tfsspring.domain.model.Presence
import com.kekouke.tfsspring.domain.model.User

fun UserDto.toDomain() = User(
    id = id,
    name = name,
    email = email,
    presence = Presence.Offline,
    avatarUrl = avatarUrl
)

fun UserEntity.toDomain() = User(
    id = id,
    name = name,
    email = email,
    presence = Presence.Offline,
    avatarUrl = avatarUrl
)

fun User.toEntity() = UserEntity(
    id = id,
    name = name,
    email = email,
    avatarUrl = avatarUrl
)