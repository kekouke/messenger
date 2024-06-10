package com.kekouke.tfsspring.domain.model

data class Page(
    val content: List<Message>,
    val foundOldest: Boolean
)