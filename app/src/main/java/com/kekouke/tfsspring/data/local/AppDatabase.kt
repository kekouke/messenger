package com.kekouke.tfsspring.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.kekouke.tfsspring.data.local.dao.ChatDao
import com.kekouke.tfsspring.data.local.dao.StreamDao
import com.kekouke.tfsspring.data.local.dao.UserDao
import com.kekouke.tfsspring.data.local.entities.MessageEntity
import com.kekouke.tfsspring.data.local.entities.ReactionEntity
import com.kekouke.tfsspring.data.local.entities.StreamEntity
import com.kekouke.tfsspring.data.local.entities.TopicEntity
import com.kekouke.tfsspring.data.local.entities.UserEntity

@Database(
    entities = [
        UserEntity::class,
        StreamEntity::class,
        TopicEntity::class,
        MessageEntity::class,
        ReactionEntity::class
    ],
    exportSchema = false,
    version = 1,
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun userDao(): UserDao
    abstract fun streamDao(): StreamDao
    abstract fun chatDao(): ChatDao
}