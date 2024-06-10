package com.kekouke.tfsspring.presentation.people.tea

import com.kekouke.tfsspring.domain.model.User
import com.kekouke.tfsspring.domain.usecases.PeopleUseCase
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flatMapMerge
import kotlinx.coroutines.flow.flow
import ru.tinkoff.kotea.core.CommandsFlowHandler
import javax.inject.Inject

private const val SERVER_POLL_TIMEOUT_IN_SECONDS = 60L

class PeopleCommandFlowHandler @Inject constructor(
    private val peopleUseCase: PeopleUseCase
) : CommandsFlowHandler<PeopleCommand, PeopleEvent.Result> {

    private var currentFilterQuery = ""
        set(value) {
            field = value.trim()
        }

    @OptIn(ExperimentalCoroutinesApi::class)
    override fun handle(commands: Flow<PeopleCommand>): Flow<PeopleEvent.Result> {
        return commands.flatMapMerge { command ->
            when (command) {
                is PeopleCommand.LoadPeople -> handleDisplayPeople()
                is PeopleCommand.FilterUsers -> handleFilterUsers(command)
            }
        }
    }

    private suspend fun handleDisplayPeople() = flow {
        val cached = peopleUseCase.getCachedUsers()
        if (cached.isNotEmpty()) {
            emit(PeopleEvent.Result.DisplayPeople(filterUsersByQuery(cached)))
        }

        peopleUseCase.getAllUsersFromNetwork().fold(
            onSuccess = { emitAll(startPollUsersPresence(it)) },
            onFailure = { error ->
                val errorEvent = if (cached.isEmpty()) {
                    PeopleEvent.Result.LoadError(error)
                } else {
                    PeopleEvent.Result.UpdateError(error)
                }

                emit(errorEvent)
            }
        )
    }

    private fun startPollUsersPresence(users: List<User>) = flow {
        while (true) {
            peopleUseCase.getAllUsersPresence().fold(
                onSuccess = { allUsersPresence ->
                    val usersWithPresence = users.map { user ->
                        allUsersPresence[user.email]?.let { presence ->
                            user.copy(presence = presence)
                        } ?: user
                    }

                    emit(PeopleEvent.Result.DisplayPeople(filterUsersByQuery(usersWithPresence)))
                },
                onFailure = { emit(PeopleEvent.Result.UpdateError(it)) }
            )

            delay(SERVER_POLL_TIMEOUT_IN_SECONDS * 1000)
        }
    }


    private fun handleFilterUsers(command: PeopleCommand.FilterUsers) = flow {
        currentFilterQuery = command.query
        val filteredUsers = filterUsersByQuery(peopleUseCase.getCachedUsers())

        emit(PeopleEvent.Result.DisplayPeople(filteredUsers))
    }

    private fun filterUsersByQuery(users: List<User>): List<User> {
        if (currentFilterQuery.isEmpty()) return users

        return users.filter { it.name.contains(currentFilterQuery, ignoreCase = true) }
    }
}