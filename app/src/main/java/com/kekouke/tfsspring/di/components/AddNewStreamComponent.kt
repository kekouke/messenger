package com.kekouke.tfsspring.di.components

import com.kekouke.tfsspring.di.FragmentScope
import com.kekouke.tfsspring.di.modules.RepositoryModule
import com.kekouke.tfsspring.presentation.streams.form.newstream.CreateStreamFragment
import dagger.Component

@Component(modules = [RepositoryModule::class], dependencies = [AppComponent::class])
@FragmentScope
interface AddNewStreamComponent {
    fun inject(fragment: CreateStreamFragment)

    @Component.Factory
    interface Factory {
        fun create(appComponent: AppComponent): AddNewStreamComponent
    }
}