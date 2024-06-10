package com.kekouke.tfsspring.presentation.streams.form.newstream.tea

sealed class CreateStreamCommand {
    data class CreateStream(val streamName: String) : CreateStreamCommand()
}