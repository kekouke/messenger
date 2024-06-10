package com.kekouke.tfsspring.presentation.people.tea

import com.github.terrakok.cicerone.Router
import com.kekouke.tfsspring.presentation.people.PeopleState
import ru.tinkoff.kotea.core.KoteaStore
import javax.inject.Inject

class PeopleStoreFactory @Inject constructor(
    private val router: Router,
    private val commandFlowHandler: PeopleCommandFlowHandler
) {

    fun create() = KoteaStore<PeopleState, PeopleEvent, PeopleEvent.UI, PeopleCommand, PeopleNews>(
        initialState = PeopleState.Initial,
        commandsFlowHandlers = listOf(commandFlowHandler),
        update = PeopleUpdate(router)
    )

}