package com.kekouke.tfsspring.presentation.chat.adapters.delegates

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.kekouke.tfsspring.presentation.chat.ChatListener
import com.kekouke.tfsspring.presentation.chat.adapters.BaseMessageViewHolder
import com.kekouke.tfsspring.domain.model.Message
import com.kekouke.tfsspring.presentation.chat.ui.SendMessageView

class SendMessageAdapterDelegate(private val chatListener: ChatListener) : AdapterDelegate {

    override fun onCreateViewHolder(parent: ViewGroup) = ViewHolder(
        SendMessageView(parent.context)
    )

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, item: DelegateItem) {
        (holder as ViewHolder).bind(item.content() as Message)
    }

    override fun isOfViewType(item: DelegateItem): Boolean {
        return item is MessageDelegateItem && !(item.content() as Message).isReceivedMessage
    }

    inner class ViewHolder(messageView: SendMessageView) :
        BaseMessageViewHolder(messageView, chatListener)
}