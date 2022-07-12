package org.ivcode.inventory.auth.repositories.model

data class UserEmailVerificationEntity(
    val userEmailVerificationId: Long? = null,
    val userId: Long,
    val salt: String,
    val code: String
)