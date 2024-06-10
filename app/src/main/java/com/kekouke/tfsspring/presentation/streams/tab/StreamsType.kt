package com.kekouke.tfsspring.presentation.streams.tab

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
enum class StreamsType : Parcelable {
    ALL_STREAMS,
    SUBSCRIBED,
}