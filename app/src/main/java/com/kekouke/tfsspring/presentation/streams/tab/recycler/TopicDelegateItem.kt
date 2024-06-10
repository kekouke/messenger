package com.kekouke.tfsspring.presentation.streams.tab.recycler

import com.kekouke.tfsspring.presentation.chat.adapters.delegates.DelegateItem
import com.kekouke.tfsspring.domain.model.Topic

class TopicDelegateItem(private val value: Topic) : DelegateItem {
    override fun id(): Int = value.hashCode()

    override fun content(): Any = value

    override fun compareToOther(item: DelegateItem): Boolean {
        return value == item.content()
    }
}