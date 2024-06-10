package com.kekouke.tfsspring.presentation.profile.tea

import com.kekouke.tfsspring.domain.model.User

sealed interface ProfileEvent {

    sealed interface UI : ProfileEvent {
        data class DisplayUser(val user: User? = null) : UI
        data object NavigateBack : UI
    }

    sealed interface Result : ProfileEvent {

        data class DisplayUser(val user: User) : Result

        data class LoadError(val throwable: Throwable) : Result
    }
}