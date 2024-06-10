package com.kekouke.tfsspring.data.api.services

import com.kekouke.tfsspring.data.api.response.RegisterEventQueueResponse
import com.kekouke.tfsspring.data.api.response.events.EventRootDto
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface LongPollingApiService {
    @POST("register")
    suspend fun registerEventQueue(@Query("event_types") eventTypes: String): RegisterEventQueueResponse

    @GET("events")
    suspend fun getEvents(
        @Query("queue_id") queueId: String,
        @Query("last_event_id") eventId: Int
    ): EventRootDto
}