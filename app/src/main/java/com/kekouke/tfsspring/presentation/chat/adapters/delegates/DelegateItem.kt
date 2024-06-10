package com.kekouke.tfsspring.presentation.chat.adapters.delegates

interface DelegateItem {
    fun id(): Int
    fun content(): Any
    fun compareToOther(item: DelegateItem): Boolean
}
