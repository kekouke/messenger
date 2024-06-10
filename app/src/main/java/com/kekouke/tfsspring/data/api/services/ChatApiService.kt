package com.kekouke.tfsspring.data.api.services

import com.kekouke.tfsspring.data.api.dto.messages.MessageDtoContainer
import retrofit2.Response
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface ChatApiService {

    @GET("messages?apply_markdown=false&client_gravatar=false")
    suspend fun getMessages(
        @Query("anchor") anchor: String,
        @Query("num_before") numBefore: Int,
        @Query("num_after") numAfter: Int,
        @Query("narrow") narrow: String,
        @Query("include_anchor") includeAnchor: Boolean
    ): MessageDtoContainer

    @POST("messages?type=stream")
    suspend fun sendMessage(
        @Query("to") streamId: Int,
        @Query("topic") topic: String,
        @Query("content") content: String
    ): Response<Void>

    @POST("messages/{message_id}/reactions")
    suspend fun addReaction(
        @Path("message_id") messageId: Int,
        @Query("emoji_name") emojiName: String
    ): Response<Void>

    @DELETE("messages/{message_id}/reactions")
    suspend fun removeReaction(
        @Path("message_id") messageId: Int,
        @Query("emoji_name") emojiName: String
    ): Response<Void>
}