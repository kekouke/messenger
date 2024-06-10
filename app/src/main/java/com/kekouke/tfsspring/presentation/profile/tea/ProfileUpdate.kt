package com.kekouke.tfsspring.presentation.profile.tea

import android.util.Log
import com.github.terrakok.cicerone.Router
import com.kekouke.tfsspring.presentation.profile.ProfileState
import ru.tinkoff.kotea.core.dsl.DslUpdate

private const val TAG = "ProfileUpdate"

class ProfileUpdate(
    private val router: Router
) : DslUpdate<ProfileState, ProfileEvent, ProfileCommand, ProfileNews>() {

    override fun NextBuilder.update(event: ProfileEvent) = when (event) {
        is ProfileEvent.UI -> handleUiEvent(event)
        is ProfileEvent.Result -> handleResultEvent(event)
    }

    private fun NextBuilder.handleUiEvent(event: ProfileEvent.UI) = when (event) {
        is ProfileEvent.UI.DisplayUser -> {
            event.user?.let {
                state { ProfileState.Content(event.user, false) }
            } ?: run {
                state { ProfileState.Loading }
                commands(ProfileCommand.LoadOwnUser)
            }
        }

        ProfileEvent.UI.NavigateBack -> {
            router.exit()
        }
    }

    private fun NextBuilder.handleResultEvent(event: ProfileEvent.Result) = when (event) {
        is ProfileEvent.Result.DisplayUser -> {
            state { ProfileState.Content(event.user, true) }
        }

        is ProfileEvent.Result.LoadError -> {
            Log.d(TAG, event.throwable.toString())

            state { ProfileState.Error }
        }
    }
}