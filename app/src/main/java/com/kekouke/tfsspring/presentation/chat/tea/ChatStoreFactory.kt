package com.kekouke.tfsspring.presentation.chat.tea

import com.kekouke.tfsspring.domain.model.Topic
import com.kekouke.tfsspring.presentation.chat.ChatState
import ru.tinkoff.kotea.core.KoteaStore
import javax.inject.Inject

class ChatStoreFactory @Inject constructor(
    private val commandFlowHandler: ChatCommandFlowHandler
) {

    fun create(topic: Topic) =
        KoteaStore<ChatState, ChatEvent, ChatEvent.UI, ChatCommand, ChatNews>(
            initialState = ChatState.Initial,
            commandsFlowHandlers = listOf(commandFlowHandler),
            update = ChatUpdate(topic)
        )
}