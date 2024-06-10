package com.kekouke.tfsspring.domain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
enum class Presence : Parcelable {
    Active,
    Idle,
    Offline;

    override fun toString(): String {
        return this.name.lowercase()
    }
}