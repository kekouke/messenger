package com.kekouke.tfsspring.presentation.people

import com.kekouke.tfsspring.domain.model.User

sealed class PeopleState {

    data object Initial : PeopleState()
    data object Loading : PeopleState()
    data object NetworkError : PeopleState()
    class Content(val users: List<User>) : PeopleState()

}