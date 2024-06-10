package com.kekouke.tfsspring.presentation.chat.adapters.delegates

import com.kekouke.tfsspring.domain.model.Date

class DateDelegateItem(private val value: Date) : DelegateItem {
    override fun id(): Int = value.id

    override fun content(): Any = value

    override fun compareToOther(item: DelegateItem): Boolean {
        return (item as DateDelegateItem).value == value
    }
}