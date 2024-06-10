package com.kekouke.tfsspring.data.api.dto.streams

import com.google.gson.annotations.SerializedName

class SubscribedStreamsDtoContainer(
    @SerializedName("subscriptions") override val streams: List<StreamDto>
) : StreamsDtoContainer

