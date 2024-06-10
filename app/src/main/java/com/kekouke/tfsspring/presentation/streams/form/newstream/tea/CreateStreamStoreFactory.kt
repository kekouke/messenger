package com.kekouke.tfsspring.presentation.streams.form.newstream.tea

import com.github.terrakok.cicerone.Router
import com.kekouke.tfsspring.presentation.streams.form.newstream.CreateStreamState
import ru.tinkoff.kotea.core.KoteaStore
import javax.inject.Inject

class CreateStreamStoreFactory @Inject constructor(
    private val router: Router,
    private val createStreamCommandFlowHandler: CreateStreamCommandFlowHandler
) {

    fun create() = KoteaStore<
            CreateStreamState,
            CreateStreamEvent,
            CreateStreamEvent.UI,
            CreateStreamCommand,
            CreateStreamNews>(
        initialState = CreateStreamState(),
        commandsFlowHandlers = listOf(createStreamCommandFlowHandler),
        update = CreateStreamUpdate(router)
    )
}