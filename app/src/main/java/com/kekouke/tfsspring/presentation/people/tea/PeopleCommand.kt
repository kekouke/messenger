package com.kekouke.tfsspring.presentation.people.tea

sealed interface PeopleCommand {
    data object LoadPeople : PeopleCommand
    data class FilterUsers(val query: String) : PeopleCommand
}