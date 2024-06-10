package com.kekouke.tfsspring.domain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Stream(
    val id: Int,
    val name: String,
    val expanded: Boolean = false,
    val topics: List<Topic>? = null
) : Parcelable