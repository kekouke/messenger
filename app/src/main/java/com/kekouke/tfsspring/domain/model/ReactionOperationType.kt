package com.kekouke.tfsspring.domain.model

private const val OPERATION_ADD = "add"
private const val OPERATION_REMOVE = "remove"

enum class ReactionOperationType {
    ADD,
    REMOVE;

    companion object {
        fun fromString(value: String) = when (value) {
            OPERATION_ADD -> ADD
            OPERATION_REMOVE -> REMOVE
            else -> error("Unknown operation type: $value")
        }
    }
}