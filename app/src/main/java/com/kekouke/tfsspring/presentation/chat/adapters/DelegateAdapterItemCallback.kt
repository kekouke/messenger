package com.kekouke.tfsspring.presentation.chat.adapters

import androidx.recyclerview.widget.DiffUtil
import com.kekouke.tfsspring.presentation.chat.adapters.delegates.DelegateItem

class DelegateAdapterItemCallback : DiffUtil.ItemCallback<DelegateItem>() {
    override fun areItemsTheSame(oldItem: DelegateItem, newItem: DelegateItem): Boolean {
        return oldItem::class == newItem::class && oldItem.id() == newItem.id()
    }

    override fun areContentsTheSame(oldItem: DelegateItem, newItem: DelegateItem): Boolean {
        return oldItem.compareToOther(newItem)
    }
}