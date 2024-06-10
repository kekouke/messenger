package com.kekouke.tfsspring.presentation.people.tea

import com.kekouke.tfsspring.domain.model.User

sealed interface PeopleEvent {

    sealed interface UI : PeopleEvent {
        data object DisplayPeopleEvent : UI
        data class NavigateToProfile(val user: User) : UI
        data class Search(val query: String): UI
    }

    sealed interface Result : PeopleEvent {
        data class DisplayPeople(val users: List<User>) : Result
        data class LoadError(val throwable: Throwable) : Result
        data class UpdateError(val throwable: Throwable) : Result
    }

}