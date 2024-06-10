package com.kekouke.tfsspring.presentation.people

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.kekouke.tfsspring.databinding.ItemUserBinding
import com.kekouke.tfsspring.domain.model.Presence
import com.kekouke.tfsspring.domain.model.User
import com.kekouke.tfsspring.R as TfSpringR

class UserAdapter : ListAdapter<User, UserAdapter.ViewHolder>(UserDiffItemCallback()) {

    var onUserClick: ((User) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ViewHolder(
        ItemUserBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
    ).apply {
        itemView.setOnClickListener { onUserClick?.invoke(getItem(adapterPosition)) }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class ViewHolder(private val binding: ItemUserBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(user: User) {
            with(binding) {
                tvUsername.text = user.name
                tvEmail.text = user.email

                Glide.with(root.context)
                    .load(user.avatarUrl)
                    .placeholder(TfSpringR.drawable.placeholder_avatar)
                    .error(TfSpringR.drawable.placeholder_avatar)
                    .into(ivAvatar)

                val statusImageResource = when (user.presence) {
                    Presence.Active -> TfSpringR.drawable.ic_online
                    Presence.Idle -> TfSpringR.drawable.ic_idle
                    Presence.Offline -> TfSpringR.drawable.ic_offline
                }
                ivStatus.setImageResource(statusImageResource)
            }
        }
    }
}