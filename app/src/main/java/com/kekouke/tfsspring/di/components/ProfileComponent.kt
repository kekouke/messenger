package com.kekouke.tfsspring.di.components

import com.kekouke.tfsspring.di.FragmentScope
import com.kekouke.tfsspring.di.modules.RepositoryModule
import com.kekouke.tfsspring.presentation.profile.ProfileFragment
import dagger.Component

@Component(modules = [RepositoryModule::class], dependencies = [AppComponent::class])
@FragmentScope
interface ProfileComponent {

    fun inject(fragment: ProfileFragment)

    @Component.Factory
    interface Factory {
        fun create(appComponent: AppComponent): ProfileComponent
    }
}