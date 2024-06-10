package com.kekouke.tfsspring.di.components

import com.kekouke.tfsspring.di.FragmentScope
import com.kekouke.tfsspring.di.modules.RepositoryModule
import com.kekouke.tfsspring.presentation.streams.tab.StreamsTabFragment
import dagger.Component

@Component(modules = [RepositoryModule::class], dependencies = [AppComponent::class])
@FragmentScope
interface StreamsTabComponent {
    fun inject(fragment: StreamsTabFragment)

    @Component.Factory
    interface Factory {
        fun create(appComponent: AppComponent): StreamsTabComponent
    }
}