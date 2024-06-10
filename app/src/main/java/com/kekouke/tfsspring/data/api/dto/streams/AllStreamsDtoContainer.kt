package com.kekouke.tfsspring.data.api.dto.streams

import com.google.gson.annotations.SerializedName

class AllStreamsDtoContainer(
    @SerializedName("streams") override val streams: List<StreamDto>
) : StreamsDtoContainer