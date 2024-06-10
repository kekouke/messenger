package com.kekouke.tfsspring.presentation.profile.tea

import com.github.terrakok.cicerone.Router
import com.kekouke.tfsspring.presentation.profile.ProfileState
import ru.tinkoff.kotea.core.KoteaStore
import javax.inject.Inject

class ProfileStoreFactory @Inject constructor(
    private val router: Router,
    private val commandFlowHandler: ProfileCommandFlowHandler
) {

    fun create() = KoteaStore<ProfileState, ProfileEvent, ProfileEvent.UI, ProfileCommand, ProfileNews>(
        initialState = ProfileState.Initial,
        commandsFlowHandlers = listOf(commandFlowHandler),
        update = ProfileUpdate(router)
    )

}