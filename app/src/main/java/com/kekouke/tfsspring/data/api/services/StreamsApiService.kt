package com.kekouke.tfsspring.data.api.services

import com.kekouke.tfsspring.data.api.dto.streams.AllStreamsDtoContainer
import com.kekouke.tfsspring.data.api.dto.streams.SubscribedStreamsDtoContainer
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface StreamsApiService {

    @GET("streams")
    suspend fun getAllStreams(): Response<AllStreamsDtoContainer>

    @GET("users/me/subscriptions")
    suspend fun getSubscribedStreams(): Response<SubscribedStreamsDtoContainer>

    @POST("users/me/subscriptions")
    suspend fun createStream(@Query("subscriptions") subscriptions: String): Response<Void>
}