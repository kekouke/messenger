package com.kekouke.tfsspring.data.local.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "message")
class MessageEntity(
    @PrimaryKey val id: Int,
    @ColumnInfo("content") val content: String,
    @ColumnInfo("senderAvatarUrl") val senderAvatarUrl: String,
    @ColumnInfo("senderFullName") val senderFullName: String,
    @ColumnInfo("senderId") val senderId: Int,
    @ColumnInfo("streamId") val streamId: Int,
    @ColumnInfo("topicName") val topicName: String,
    @ColumnInfo("timestampMilliseconds") val timestampMilliseconds: Long,
)