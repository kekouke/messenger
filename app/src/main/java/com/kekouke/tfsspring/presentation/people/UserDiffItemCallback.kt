package com.kekouke.tfsspring.presentation.people

import androidx.recyclerview.widget.DiffUtil
import com.kekouke.tfsspring.domain.model.User

class UserDiffItemCallback : DiffUtil.ItemCallback<User>() {
    override fun areItemsTheSame(oldItem: User, newItem: User): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: User, newItem: User): Boolean {
        return oldItem == newItem
    }
}