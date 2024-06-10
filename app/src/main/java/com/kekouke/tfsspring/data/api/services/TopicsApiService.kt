package com.kekouke.tfsspring.data.api.services

import com.kekouke.tfsspring.data.api.dto.topics.TopicDtoContainer
import retrofit2.http.GET
import retrofit2.http.Path

interface TopicsApiService {

    @GET("users/me/{stream_id}/topics")
    suspend fun getTopics(@Path("stream_id") streamId: Int): TopicDtoContainer

}