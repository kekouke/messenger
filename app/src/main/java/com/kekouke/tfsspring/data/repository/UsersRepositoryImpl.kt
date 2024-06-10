package com.kekouke.tfsspring.data.repository

import com.kekouke.tfsspring.data.api.dto.users.UserDto
import com.kekouke.tfsspring.data.api.services.UsersApiService
import com.kekouke.tfsspring.data.api.response.presence.PresenceDetail
import com.kekouke.tfsspring.data.local.dao.UserDao
import com.kekouke.tfsspring.data.local.entities.UserEntity
import com.kekouke.tfsspring.data.mappers.toDomain
import com.kekouke.tfsspring.data.mappers.toEntity
import com.kekouke.tfsspring.domain.model.Presence
import com.kekouke.tfsspring.domain.model.User
import com.kekouke.tfsspring.domain.repository.UsersRepository
import com.kekouke.tfsspring.runCatchingNonCancellation
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.withContext
import javax.inject.Inject

class UsersRepositoryImpl @Inject constructor(
    private val userDao: UserDao,
    private val api: UsersApiService
) : UsersRepository {

    override suspend fun getAllUsersFromNetwork(): Result<List<User>> =
        withContext(Dispatchers.IO) {
            runCatchingNonCancellation {
                api.getAllUsers().users.asFlow()
                    .filter { it.isActive && !it.isBot }
                    .map(UserDto::toDomain)
                    .toList()
                    .sortedBy(User::name)
                    .also { users ->
                        userDao.addUsers(users.map(User::toEntity))
                    }
            }
        }

    override suspend fun getAllUsersPresence() = withContext(Dispatchers.IO) {
        runCatchingNonCancellation {
            val allUsersPresenceResponse = api.getAllUsersPresence()
            val serverTimestamp = allUsersPresenceResponse.serverTimestamp.toLong()
            val allUsersPresence = allUsersPresenceResponse.presences

            allUsersPresence.mapValues { emailToPresence ->
                getPresenceStatus(serverTimestamp, emailToPresence.value.detail)
            }
        }
    }

    override suspend fun getCachedUsers(): List<User> = withContext(Dispatchers.IO) {
        userDao.getAllUsers()
            .map(UserEntity::toDomain)
            .sortedBy(User::name)
    }

    override suspend fun getOwnUser(): User {
        val response = api.getOwnUser()
        return response.toDomain()
    }

    private fun getPresenceStatus(serverTimestamp: Long, detail: PresenceDetail) = if (
        serverTimestamp - detail.timestamp > PRESENCE_OFFLINE_THRESHOLD_SECONDS
    ) {
        Presence.Offline
    } else {
        presenceStringToEnum(detail.status)
    }

    private fun presenceStringToEnum(presence: String) = when (presence) {
        ACTIVE -> Presence.Active
        IDLE -> Presence.Idle
        else -> throw RuntimeException("Unsupported type of Presence")
    }

    companion object {
        private const val ACTIVE = "active"
        private const val IDLE = "idle"
        private const val PRESENCE_OFFLINE_THRESHOLD_SECONDS = 200
    }
}