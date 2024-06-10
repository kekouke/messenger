package com.kekouke.tfsspring.presentation.people.tea

import android.util.Log
import com.github.terrakok.cicerone.Router
import com.kekouke.tfsspring.navigation.Screens
import com.kekouke.tfsspring.presentation.people.PeopleState
import ru.tinkoff.kotea.core.dsl.DslUpdate

private const val TAG = "PeopleUpdate"

class PeopleUpdate(
    private val router: Router
) : DslUpdate<PeopleState, PeopleEvent, PeopleCommand, PeopleNews>() {

    override fun NextBuilder.update(event: PeopleEvent) = when (event) {
        is PeopleEvent.UI -> handleUiEvent(event)
        is PeopleEvent.Result -> handleResultEvent(event)
    }

    private fun NextBuilder.handleUiEvent(event: PeopleEvent.UI) = when (event) {
        is PeopleEvent.UI.DisplayPeopleEvent -> {
            state { PeopleState.Loading }
            commands(PeopleCommand.LoadPeople)
        }

        is PeopleEvent.UI.NavigateToProfile -> {
            router.navigateTo(Screens.Profile(event.user))
        }

        is PeopleEvent.UI.Search -> {
            commands(PeopleCommand.FilterUsers(event.query))
        }
    }

    private fun NextBuilder.handleResultEvent(event: PeopleEvent.Result) = when (event) {
        is PeopleEvent.Result.DisplayPeople -> {
            state { PeopleState.Content(event.users) }
        }

        is PeopleEvent.Result.LoadError -> {
            Log.d(TAG, event.throwable.toString())

            state { PeopleState.NetworkError }
        }

        is PeopleEvent.Result.UpdateError -> {
            Log.d(TAG, event.throwable.toString())

            news(PeopleNews.ShowErrorToast)
        }
    }
}