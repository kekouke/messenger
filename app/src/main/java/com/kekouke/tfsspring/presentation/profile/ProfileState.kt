package com.kekouke.tfsspring.presentation.profile

import com.kekouke.tfsspring.domain.model.User

sealed class ProfileState {
    data object Initial : ProfileState()
    data object Loading : ProfileState()
    data object Error : ProfileState()
    class Content(val user: User, val isOwnUser: Boolean) : ProfileState()
}