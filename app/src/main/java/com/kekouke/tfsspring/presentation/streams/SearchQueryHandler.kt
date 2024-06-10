package com.kekouke.tfsspring.presentation.streams

import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow

object SearchQueryHandler {

    private val _searchQueryFlow by lazy {
        MutableSharedFlow<String>(replay = 1)
    }

    val searchQueryFlow = _searchQueryFlow.asSharedFlow()

    suspend fun push(query: String) = _searchQueryFlow.emit(query)

}