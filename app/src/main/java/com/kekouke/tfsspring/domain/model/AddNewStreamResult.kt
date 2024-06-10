package com.kekouke.tfsspring.domain.model

sealed class AddNewStreamResult {
    data object Success : AddNewStreamResult()
    data object AlreadyExistsError : AddNewStreamResult()
    data class NetworkError(val throwable: Throwable) : AddNewStreamResult()
    data class RefreshStreamsError(val throwable: Throwable) : AddNewStreamResult()
}