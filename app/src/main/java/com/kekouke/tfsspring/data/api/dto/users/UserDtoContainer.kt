package com.kekouke.tfsspring.data.api.dto.users

import com.google.gson.annotations.SerializedName

class UserDtoContainer(
    @SerializedName("members") val users: List<UserDto>,
)