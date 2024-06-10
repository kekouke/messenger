package com.kekouke.tfsspring.presentation.streams.tab.recycler

import com.kekouke.tfsspring.presentation.chat.adapters.delegates.DelegateItem
import com.kekouke.tfsspring.domain.model.Stream

class StreamDelegateItem(private val value: Stream) : DelegateItem {
    override fun id(): Int = value.id

    override fun content(): Any = value

    override fun compareToOther(item: DelegateItem): Boolean {
        return value == item.content()
    }
}