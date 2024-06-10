package com.kekouke.tfsspring.domain.model

private const val OPERATION_CREATE = "create"
private const val OPERATION_DELETE = "delete"
private const val OPERATION_UPDATE = "update"

enum class StreamOperationType {
    CREATE,
    DELETE,
    UPDATE;

    companion object {
        fun fromString(value: String) = when (value) {
            OPERATION_CREATE -> CREATE
            OPERATION_DELETE -> DELETE
            OPERATION_UPDATE -> UPDATE
            else -> error("Unknown operation type: $value")
        }
    }
}