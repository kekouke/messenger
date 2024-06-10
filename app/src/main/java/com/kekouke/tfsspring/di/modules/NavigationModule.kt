package com.kekouke.tfsspring.di.modules

import com.github.terrakok.cicerone.Cicerone
import com.github.terrakok.cicerone.Router
import com.kekouke.tfsspring.navigation.TabRouter
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class NavigationModule {
    @Provides
    @Singleton
    fun provideMainNavigationRouter() = Router()

    @Provides
    @Singleton
    fun provideTabNavigationRouter() = TabRouter()

    @Provides
    @Singleton
    fun provideMainNavigation(router: Router): Cicerone<Router> = Cicerone.create(router)

    @Provides
    @Singleton
    fun provideTabNavigation(router: TabRouter): Cicerone<TabRouter> = Cicerone.create(router)
}