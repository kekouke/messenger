package com.kekouke.tfsspring.presentation.profile.tea

import com.kekouke.tfsspring.domain.usecases.PeopleUseCase
import com.kekouke.tfsspring.runCatchingNonCancellation
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import ru.tinkoff.kotea.core.CommandsFlowHandler
import javax.inject.Inject

class ProfileCommandFlowHandler @Inject constructor(
    private val peopleUseCase: PeopleUseCase
) : CommandsFlowHandler<ProfileCommand, ProfileEvent.Result> {

    @OptIn(ExperimentalCoroutinesApi::class)
    override fun handle(commands: Flow<ProfileCommand>): Flow<ProfileEvent.Result> {
        return commands.flatMapLatest { command ->
            when (command) {
                is ProfileCommand.LoadOwnUser -> {
                    flow {
                        runCatchingNonCancellation { peopleUseCase.getOwnUser() }.fold(
                            onSuccess = { emit(ProfileEvent.Result.DisplayUser(it)) },
                            onFailure = { emit(ProfileEvent.Result.LoadError(it)) }
                        )

                    }
                }
            }
        }
    }
}