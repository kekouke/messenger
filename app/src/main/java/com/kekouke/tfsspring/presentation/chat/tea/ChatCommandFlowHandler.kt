package com.kekouke.tfsspring.presentation.chat.tea

import com.kekouke.tfsspring.domain.model.Message
import com.kekouke.tfsspring.domain.model.PollEvent
import com.kekouke.tfsspring.domain.model.Reaction
import com.kekouke.tfsspring.domain.model.ReactionOperationType
import com.kekouke.tfsspring.domain.model.Topic
import com.kekouke.tfsspring.domain.usecases.ChatUseCase
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flatMapMerge
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.last
import kotlinx.coroutines.flow.onEach
import ru.tinkoff.kotea.core.CommandsFlowHandler
import javax.inject.Inject

class ChatCommandFlowHandler @Inject constructor(
    private val chatUseCase: ChatUseCase
) : CommandsFlowHandler<ChatCommand, ChatEvent.Result> {

    private val chatMessages = mutableListOf<Message>()

    var gotLastPage = true
        private set

    @OptIn(ExperimentalCoroutinesApi::class)
    override fun handle(commands: Flow<ChatCommand>): Flow<ChatEvent.Result> {
        return commands.flatMapMerge { command ->
            when (command) {
                is ChatCommand.ShowChat -> handleShowChat(command)
                is ChatCommand.LoadNextPage -> handleLoadNextPage(command)
                is ChatCommand.SendMessage -> handleSendMessage(command)
                is ChatCommand.AddReaction -> handleAddReaction(command)
                is ChatCommand.RemoveReaction -> handleRemoveReaction(command)
            }
        }
    }

    private fun handleShowChat(command: ChatCommand.ShowChat) = flow {
        val pageFromNetwork = chatUseCase.getMessages(command.topic)
            .onEach { result ->
                result.getOrNull()?.run {
                    emit(ChatEvent.Result.ShowChat(content))
                }

                result.exceptionOrNull()?.run {
                    emit(ChatEvent.Result.LoadMessagesError(this))
                }
            }
            .last()

        pageFromNetwork.getOrNull()?.run {
            gotLastPage = foundOldest

            modifyAndSaveMessages(command.topic) {
                clear()
                addAll(content)
            }

            chatUseCase.subscribeOnChatEvents(command.topic)
                .catch { emit(ChatEvent.Result.RegisterEventQueueError(it)) }
                .collect { event ->
                    when (event) {
                        is PollEvent.Chat.MessageEvent -> handleMessageEvent(event, command)
                        is PollEvent.Chat.ReactionEvent -> handleReactionEvent(event, command)
                    }
                }
        }
    }

    private suspend fun FlowCollector<ChatEvent.Result>.handleMessageEvent(
        event: PollEvent.Chat.MessageEvent,
        command: ChatCommand.ShowChat
    ) {
        modifyAndSaveMessages(command.topic) {
            add(event.message)
        }

        val resultEvent = if (event.message.isReceivedMessage) {
            ChatEvent.Result.UpdateChat(chatMessages.toList())
        } else {
            ChatEvent.Result.SendMessageSuccess(chatMessages.toList())
        }

        emit(resultEvent)
    }

    private suspend fun FlowCollector<ChatEvent.Result>.handleReactionEvent(
        event: PollEvent.Chat.ReactionEvent,
        command: ChatCommand.ShowChat
    ) {
        chatMessages.indexOfFirst { it.id == event.messageId }.let { index ->
            if (index < 0) return@let

            val updatedMessage = chatMessages[index].run {
                when (event.operation) {
                    ReactionOperationType.ADD -> {
                        addReaction(event.reaction, event.isMeReaction)
                    }

                    ReactionOperationType.REMOVE -> {
                        removeReaction(event.reaction, event.isMeReaction)
                    }
                }
            }

            modifyAndSaveMessages(command.topic) {
                set(index, updatedMessage)
            }

            emit(ChatEvent.Result.UpdateChat(chatMessages.toList()))
        }
    }

    private fun Message.addReaction(reaction: Reaction, isMeReaction: Boolean): Message {
        reactions.indexOfFirst { it.code == reaction.code }.let { index ->
            val updatedReactions = reactions.toMutableList().apply {
                elementAtOrNull(index)?.run {
                    val selectedNow = if (isMeReaction) true else selected
                    set(index, copy(count = count + 1, selected = selectedNow))
                } ?: add(reaction)
            }

            return copy(reactions = updatedReactions)
        }
    }

    private fun Message.removeReaction(reaction: Reaction, isMeReaction: Boolean): Message {
        reactions.indexOfFirst { it.code == reaction.code }.let { index ->
            val updatedReactions = reactions.toMutableList().apply {
                elementAtOrNull(index)?.run {
                    if (count == 1) {
                        removeAt(index)
                    } else {
                        val selectedNow = if (isMeReaction) false else selected
                        set(index, copy(count = count - 1, selected = selectedNow))
                    }
                }
            }

            return this.copy(reactions = updatedReactions)
        }
    }

    private fun handleLoadNextPage(command: ChatCommand.LoadNextPage) = flow {
        if (gotLastPage) return@flow

        emit(ChatEvent.Result.StartProcess)

        chatUseCase.loadNextPage(command.topic).collect { result ->
            result.fold(
                onSuccess = { page ->
                    gotLastPage = page.foundOldest
                    chatMessages.addAll(0, page.content)

                    emit(ChatEvent.Result.UpdateChat(chatMessages.toList()))
                },
                onFailure = { emit(ChatEvent.Result.LoadMessagesError(it)) }
            )
        }
    }

    private fun handleSendMessage(command: ChatCommand.SendMessage): Flow<ChatEvent.Result> = flow {
        val topic = command.topic
        chatUseCase.sendMessage(topic.streamId, topic.name, command.text).onFailure {
            emit(ChatEvent.Result.SendMessageError(it))
        }
    }

    private fun handleAddReaction(command: ChatCommand.AddReaction): Flow<ChatEvent.Result> = flow {
        chatUseCase.addReaction(command.messageId, command.reaction).onFailure {
            emit(ChatEvent.Result.ReactionError(it))
        }
    }

    private fun handleRemoveReaction(command: ChatCommand.RemoveReaction): Flow<ChatEvent.Result> =
        flow {
            chatUseCase.removeReaction(command.messageId, command.reaction).onFailure {
                emit(ChatEvent.Result.ReactionError(it))
            }
        }

    private suspend inline fun modifyAndSaveMessages(
        topic: Topic,
        modify: MutableList<Message>.() -> Unit
    ) {
        chatMessages.modify()
        chatUseCase.saveMessages(chatMessages, topic)
    }
}