package com.kekouke.tfsspring.presentation.chat.adapters.delegates

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.kekouke.tfsspring.domain.model.Message
import com.kekouke.tfsspring.presentation.chat.ChatListener
import com.kekouke.tfsspring.presentation.chat.adapters.BaseMessageViewHolder
import com.kekouke.tfsspring.presentation.chat.ui.ReceivedMessageView

class ReceivedMessageAdapterDelegate(private val chatListener: ChatListener) : AdapterDelegate {

    override fun onCreateViewHolder(parent: ViewGroup) = ViewHolder(
        ReceivedMessageView(parent.context)
    )

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, item: DelegateItem) {
        (holder as ViewHolder).bind(item.content() as Message)
    }

    override fun isOfViewType(item: DelegateItem): Boolean {
        return item is MessageDelegateItem && (item.content() as Message).isReceivedMessage
    }

    inner class ViewHolder(private val messageView: ReceivedMessageView) :
        BaseMessageViewHolder(messageView, chatListener) {

        override fun bind(message: Message) {
            super.bind(message)
            messageView.username = message.username
            messageView.setAvatar(message.userAvatarUrl)
        }
    }
}