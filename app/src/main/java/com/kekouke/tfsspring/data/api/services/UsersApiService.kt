package com.kekouke.tfsspring.data.api.services

import com.kekouke.tfsspring.data.api.dto.users.UserDtoContainer
import com.kekouke.tfsspring.data.api.response.presence.AllUsersPresenceResponse
import com.kekouke.tfsspring.data.api.dto.users.UserDto
import retrofit2.http.GET

interface UsersApiService {

    @GET("users")
    suspend fun getAllUsers(): UserDtoContainer

    @GET("realm/presence")
    suspend fun getAllUsersPresence(): AllUsersPresenceResponse

    @GET("users/me")
    suspend fun getOwnUser(): UserDto
}