package com.kekouke.tfsspring.di.components

import com.kekouke.tfsspring.presentation.chat.ChatFragment
import com.kekouke.tfsspring.di.FragmentScope
import com.kekouke.tfsspring.di.modules.RepositoryModule
import dagger.Component

@Component(modules = [RepositoryModule::class], dependencies = [AppComponent::class])
@FragmentScope
interface ChatComponent {
    fun inject(fragment: ChatFragment)

    @Component.Factory
    interface Factory {
        fun create(appComponent: AppComponent): ChatComponent
    }
}