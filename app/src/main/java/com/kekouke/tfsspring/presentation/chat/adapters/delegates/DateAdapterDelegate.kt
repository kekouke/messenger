package com.kekouke.tfsspring.presentation.chat.adapters.delegates

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.kekouke.tfsspring.domain.model.Date
import com.kekouke.tfsspring.databinding.MessageDateBinding

class DateAdapterDelegate : AdapterDelegate {
    override fun onCreateViewHolder(parent: ViewGroup) = ViewHolder(
        MessageDateBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
    )

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, item: DelegateItem) {
        (holder as ViewHolder).bind(item.content() as Date)
    }

    override fun isOfViewType(item: DelegateItem): Boolean {
        return item is DateDelegateItem
    }

    class ViewHolder(private val binding: MessageDateBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(dateModel: Date) {
            binding.tvDate.text = dateModel.value
        }
    }
}