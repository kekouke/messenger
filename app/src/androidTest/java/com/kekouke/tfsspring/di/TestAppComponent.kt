package com.kekouke.tfsspring.di

import android.content.Context
import com.kekouke.tfsspring.di.components.AppComponent
import com.kekouke.tfsspring.di.modules.NavigationModule
import com.kekouke.tfsspring.di.modules.TestDatabaseModule
import com.kekouke.tfsspring.di.modules.TestNetworkModule
import dagger.BindsInstance
import dagger.Component
import javax.inject.Singleton

@Component(modules = [TestNetworkModule::class, NavigationModule::class, TestDatabaseModule::class])
@Singleton
interface TestAppComponent : AppComponent {
    @Component.Factory
    interface Factory {
        fun create(@BindsInstance context: Context): TestAppComponent
    }
}