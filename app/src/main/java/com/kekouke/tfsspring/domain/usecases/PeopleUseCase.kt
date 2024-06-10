package com.kekouke.tfsspring.domain.usecases

import com.kekouke.tfsspring.domain.model.Presence
import com.kekouke.tfsspring.domain.repository.UsersRepository
import javax.inject.Inject

class PeopleUseCase @Inject constructor (private val usersRepository: UsersRepository) {

    suspend fun getAllUsersFromNetwork() = usersRepository.getAllUsersFromNetwork()

    suspend fun getOwnUser() = usersRepository.getOwnUser()

    suspend fun getCachedUsers() = usersRepository.getCachedUsers()

    suspend fun getAllUsersPresence(): Result<Map<String, Presence>> = usersRepository.getAllUsersPresence()
}