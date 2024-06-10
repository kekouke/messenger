package com.kekouke.tfsspring.presentation.streams.form.newstream.tea

sealed class CreateStreamEvent {

    sealed class UI : CreateStreamEvent() {
        data object NavigateBack : UI()
        data class CreateStream(val name: String) : UI()
    }

    sealed class Result : CreateStreamEvent() {
        data object CreateStreamSuccess : Result()
        data object AlreadyExistsError : Result()
        data class NetworkError(val throwable: Throwable) : Result()
        data class RefreshStreamsError(val throwable: Throwable) : Result()
    }
}