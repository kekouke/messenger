package com.kekouke.tfsspring.presentation.streams.tab.recycler

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.kekouke.tfsspring.presentation.chat.adapters.delegates.AdapterDelegate
import com.kekouke.tfsspring.presentation.chat.adapters.delegates.DelegateItem
import com.kekouke.tfsspring.databinding.ItemStreamBinding
import com.kekouke.tfsspring.domain.model.Stream
import com.kekouke.tfsspring.presentation.streams.tab.StreamsListener
import com.kekouke.tfsspring.R as TfSpringR

class StreamAdapterDelegate(private val listener: StreamsListener) : AdapterDelegate {

    override fun onCreateViewHolder(parent: ViewGroup): RecyclerView.ViewHolder = ViewHolder(
        ItemStreamBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
    )

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, item: DelegateItem) {
        (holder as ViewHolder).bind(item.content() as Stream)
    }

    override fun isOfViewType(item: DelegateItem): Boolean {
        return item is StreamDelegateItem
    }

    inner class ViewHolder(private val binding: ItemStreamBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(stream: Stream) {
            binding.tvChannelName.text = stream.name
            itemView.setOnClickListener {
                listener.onStreamClick(stream)
            }

            val imageResource = if (stream.expanded) {
                TfSpringR.drawable.ic_arrow_up
            } else {
                TfSpringR.drawable.ic_arrow_down
            }
            binding.ivExpanded.setImageResource(imageResource)
        }
    }
}