package com.kekouke.tfsspring.presentation.chat.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.kekouke.tfsspring.domain.model.Reaction
import com.kekouke.tfsspring.R as TfSpringR

class EmojiAdapter(private val reactions: List<Reaction>) :
    RecyclerView.Adapter<EmojiAdapter.ViewHolder>() {

    var onEmojiClick: ((Reaction) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ViewHolder(
        LayoutInflater.from(parent.context).inflate(TfSpringR.layout.item_emoji, parent, false)
    ).apply {
        itemView.setOnClickListener { onEmojiClick?.invoke(reactions[adapterPosition]) }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.content.text = reactions[position].code
    }

    override fun getItemCount(): Int {
        return reactions.size
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val content = view as TextView
    }
}