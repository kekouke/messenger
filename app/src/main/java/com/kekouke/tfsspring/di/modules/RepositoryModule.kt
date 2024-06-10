package com.kekouke.tfsspring.di.modules

import com.kekouke.tfsspring.data.repository.ChatRepositoryImpl
import com.kekouke.tfsspring.data.repository.StreamsRepositoryImpl
import com.kekouke.tfsspring.data.repository.UsersRepositoryImpl
import com.kekouke.tfsspring.domain.repository.ChatRepository
import com.kekouke.tfsspring.domain.repository.StreamsRepository
import com.kekouke.tfsspring.domain.repository.UsersRepository
import dagger.Binds
import dagger.Module

@Module
interface RepositoryModule {

    @Binds
    fun bindUsersRepository(repositoryImpl: UsersRepositoryImpl): UsersRepository

    @Binds
    fun bindStreamsRepository(repositoryImpl: StreamsRepositoryImpl): StreamsRepository

    @Binds
    fun bindChatRepository(repositoryImpl: ChatRepositoryImpl): ChatRepository
}