package com.kekouke.tfsspring.data.local.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "reaction", foreignKeys = [
        ForeignKey(
            entity = MessageEntity::class,
            parentColumns = ["id"],
            childColumns = ["messageId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class ReactionEntity(
    @ColumnInfo("messageId") val messageId: Int,
    @ColumnInfo("code") val code: String,
    @ColumnInfo("name") val name: String,
    @ColumnInfo("count") val count: Int,
    @ColumnInfo("selected") val selected: Boolean,
    @PrimaryKey(autoGenerate = true) @ColumnInfo("id") val id: Int = 0,
)