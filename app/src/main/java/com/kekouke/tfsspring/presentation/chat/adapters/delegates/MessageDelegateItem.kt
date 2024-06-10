package com.kekouke.tfsspring.presentation.chat.adapters.delegates

import com.kekouke.tfsspring.domain.model.Message

class MessageDelegateItem(private val value: Message) : DelegateItem {

    override fun id(): Int = value.id

    override fun content(): Any = value

    override fun compareToOther(item: DelegateItem): Boolean {
        return (item as MessageDelegateItem).value == value
    }
}