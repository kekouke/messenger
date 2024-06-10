package com.kekouke.tfsspring.data.local.entities

import androidx.room.ColumnInfo
import androidx.room.Entity

@Entity(tableName = "topic", primaryKeys = ["streamId", "name"])
class TopicEntity(
    @ColumnInfo("name") val name: String,
    @ColumnInfo("streamId") val streamId: Int,
    @ColumnInfo("streamName") val streamName: String
)