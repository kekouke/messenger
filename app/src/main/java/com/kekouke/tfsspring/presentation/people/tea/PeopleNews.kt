package com.kekouke.tfsspring.presentation.people.tea

sealed interface PeopleNews {
    data object ShowErrorToast : PeopleNews
}