package org.ivcode.inventory.auth.repositories.model

import java.util.*

data class UserPasswordResetEntity (
    val userPasswordResetId: Long? = null,
    val userId: Long,
    val salt: String,
    val code: String,
    val expiration: Date
)