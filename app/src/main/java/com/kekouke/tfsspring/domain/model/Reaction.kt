package com.kekouke.tfsspring.domain.model

data class Reaction(
    val name: String,
    val code: String,
    val count: Int = 1,
    val selected: Boolean = true
)