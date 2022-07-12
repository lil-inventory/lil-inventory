package org.ivcode.inventory.controller.model

data class UserRequest (
    val email: String,
    val displayName: String,
    val password: String
)