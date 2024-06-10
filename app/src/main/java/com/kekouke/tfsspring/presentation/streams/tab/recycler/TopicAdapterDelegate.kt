package com.kekouke.tfsspring.presentation.streams.tab.recycler

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.kekouke.tfsspring.presentation.chat.adapters.delegates.AdapterDelegate
import com.kekouke.tfsspring.presentation.chat.adapters.delegates.DelegateItem
import com.kekouke.tfsspring.databinding.ItemTopicBinding
import com.kekouke.tfsspring.domain.model.Topic
import com.kekouke.tfsspring.presentation.streams.tab.StreamsListener

class TopicAdapterDelegate(private val listener: StreamsListener) : AdapterDelegate {

    override fun onCreateViewHolder(parent: ViewGroup): RecyclerView.ViewHolder = ViewHolder(
        ItemTopicBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
    )

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, item: DelegateItem) {
        (holder as TopicAdapterDelegate.ViewHolder).bind(item.content() as Topic)
    }

    override fun isOfViewType(item: DelegateItem): Boolean {
        return item is TopicDelegateItem
    }

    inner class ViewHolder(private val binding: ItemTopicBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(topic: Topic) {
            binding.root.setBackgroundColor(calculateBackgroundColor(topic.name))
            binding.tvTopicName.text = topic.name

            itemView.setOnClickListener {
                listener.onTopicClick(topic)
            }
        }

        private fun calculateBackgroundColor(str: String): Int {
            val hash: Int = str.fold(0) { acc, ch ->
                acc + ch.code +  ((acc shl 5) - acc)
            }

            val colorString = (0 until 3).fold("#") { acc, i ->
                acc + String.format("%02x", (hash shr (i * 8)) and 0xFF)
            }

            return Color.parseColor(colorString)
        }
    }
}