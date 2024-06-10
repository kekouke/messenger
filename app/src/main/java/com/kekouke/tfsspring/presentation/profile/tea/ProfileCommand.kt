package com.kekouke.tfsspring.presentation.profile.tea

sealed interface ProfileCommand {
    data object LoadOwnUser : ProfileCommand
}