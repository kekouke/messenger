package com.kekouke.tfsspring.di.components

import com.kekouke.tfsspring.di.FragmentScope
import com.kekouke.tfsspring.presentation.streams.StreamsFragment
import dagger.Component

@Component(dependencies = [AppComponent::class])
@FragmentScope
interface StreamsComponent {

    fun inject(fragment: StreamsFragment)

    @Component.Factory
    interface Factory {
        fun create(appComponent: AppComponent): StreamsComponent
    }
}