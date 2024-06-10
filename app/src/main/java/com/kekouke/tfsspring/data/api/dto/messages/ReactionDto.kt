package com.kekouke.tfsspring.data.api.dto.messages

import com.google.gson.annotations.SerializedName

class ReactionDto(
    @SerializedName("emoji_name") val emojiName: String,
    @SerializedName("emoji_code") val emojiCode: String,
    @SerializedName("reaction_type") val reactionType: String,
    @SerializedName("user_id") val userId: Int
)