package com.kekouke.tfsspring.domain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Topic(
    val name: String,
    val streamId: Int,
    val streamName: String
) : Parcelable