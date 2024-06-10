package com.kekouke.tfsspring.di.components

import com.kekouke.tfsspring.di.FragmentScope
import com.kekouke.tfsspring.di.modules.RepositoryModule
import com.kekouke.tfsspring.presentation.people.PeopleFragment
import dagger.Component

@Component(modules = [RepositoryModule::class], dependencies = [AppComponent::class])
@FragmentScope
interface PeopleComponent {
    fun inject(fragment: PeopleFragment)

    @Component.Factory
    interface Factory {
        fun create(appComponent: AppComponent): PeopleComponent
    }
}