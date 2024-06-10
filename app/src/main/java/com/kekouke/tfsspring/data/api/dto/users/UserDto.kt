package com.kekouke.tfsspring.data.api.dto.users

import com.google.gson.annotations.SerializedName

class UserDto(
    @SerializedName("user_id") val id: Int,
    @SerializedName("full_name") val name: String,
    @SerializedName("email") val email: String,
    @SerializedName("avatar_url") val avatarUrl: String?,
    @SerializedName("is_active") val isActive: Boolean,
    @SerializedName("is_bot") val isBot: Boolean,
)