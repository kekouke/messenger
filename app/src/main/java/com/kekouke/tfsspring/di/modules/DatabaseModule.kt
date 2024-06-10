package com.kekouke.tfsspring.di.modules

import android.content.Context
import androidx.room.Room
import com.kekouke.tfsspring.data.local.AppDatabase
import com.kekouke.tfsspring.data.local.dao.ChatDao
import com.kekouke.tfsspring.data.local.dao.StreamDao
import com.kekouke.tfsspring.data.local.dao.UserDao
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

private const val DB_NAME = "tfspring.db"

@Module
class DatabaseModule {

    @Provides
    @Singleton
    fun provideAppDatabase(context: Context): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            DB_NAME
        )
        .build()
    }

    @Provides
    fun provideUserDao(db: AppDatabase): UserDao = db.userDao()

    @Provides
    fun provideStreamDao(db: AppDatabase): StreamDao = db.streamDao()

    @Provides
    fun provideChatDao(db: AppDatabase): ChatDao = db.chatDao()
}