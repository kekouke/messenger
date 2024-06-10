package com.kekouke.tfsspring.di.components

import android.content.Context
import com.github.terrakok.cicerone.Router
import com.kekouke.tfsspring.MainActivity
import com.kekouke.tfsspring.MainFragment
import com.kekouke.tfsspring.data.api.services.ChatApiService
import com.kekouke.tfsspring.data.api.services.LongPollingApiService
import com.kekouke.tfsspring.data.api.services.StreamsApiService
import com.kekouke.tfsspring.data.api.services.TopicsApiService
import com.kekouke.tfsspring.data.api.services.UsersApiService
import com.kekouke.tfsspring.data.local.dao.ChatDao
import com.kekouke.tfsspring.data.local.dao.StreamDao
import com.kekouke.tfsspring.data.local.dao.UserDao
import com.kekouke.tfsspring.di.modules.DatabaseModule
import com.kekouke.tfsspring.di.modules.NavigationModule
import com.kekouke.tfsspring.di.modules.NetworkModule
import com.kekouke.tfsspring.navigation.TabRouter
import dagger.BindsInstance
import dagger.Component
import javax.inject.Singleton

@Component(modules = [NetworkModule::class, NavigationModule::class, DatabaseModule::class])
@Singleton
interface AppComponent {
    fun inject(activity: MainActivity)
    fun inject(fragment: MainFragment)

    val usersApiService: UsersApiService
    val streamsApiService: StreamsApiService
    val topicsApiService: TopicsApiService
    val chatApiService: ChatApiService
    val longPollingApiService: LongPollingApiService

    val userDao: UserDao
    val streamDao: StreamDao
    val chatDao: ChatDao

    val mainRouter: Router
    val tabRouter: TabRouter

    @Component.Factory
    interface Factory {
        fun create(@BindsInstance context: Context): AppComponent
    }
}