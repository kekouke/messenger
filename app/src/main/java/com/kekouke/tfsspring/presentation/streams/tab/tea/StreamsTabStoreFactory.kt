package com.kekouke.tfsspring.presentation.streams.tab.tea

import com.github.terrakok.cicerone.Router
import com.kekouke.tfsspring.presentation.streams.tab.StreamsTabState
import com.kekouke.tfsspring.presentation.streams.tab.StreamsType
import ru.tinkoff.kotea.core.KoteaStore
import javax.inject.Inject

class StreamsTabStoreFactory @Inject constructor(
    private val router: Router,
    private val commandFlowHandler: StreamsTabCommandFlowHandler
) {

    fun create(streamsType: StreamsType) = KoteaStore<StreamsTabState, StreamsTabEvent, StreamsTabEvent.UI, StreamsTabCommand, StreamsTabNews>(
            initialState = StreamsTabState.Initial,
            commandsFlowHandlers = listOf(commandFlowHandler),
            update = StreamTabUpdate(router, streamsType)
        )

}