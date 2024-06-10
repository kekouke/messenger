package com.kekouke.tfsspring.presentation.streams.form.newstream.tea

sealed class CreateStreamNews {

    data object EmptyStreamNameError : CreateStreamNews()
    data object AlreadyExistsError : CreateStreamNews()
    data object NetworkError : CreateStreamNews()
    data object RefreshStreamsError : CreateStreamNews()
}