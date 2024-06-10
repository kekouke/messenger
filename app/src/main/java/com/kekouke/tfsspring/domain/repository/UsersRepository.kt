package com.kekouke.tfsspring.domain.repository

import com.kekouke.tfsspring.domain.model.Presence
import com.kekouke.tfsspring.domain.model.User

interface UsersRepository {
    suspend fun getAllUsersFromNetwork(): Result<List<User>>
    suspend fun getCachedUsers(): List<User>
    suspend fun getOwnUser(): User
    suspend fun getAllUsersPresence(): Result<Map<String, Presence>>
}