package org.ivcode.inventory.auth.repositories.model

data class UserEntity(
    val userId: Long? = null,
    val email: String,
    val emailVerified: Boolean,
    val displayName: String,
    val superAdmin: Boolean,
    val accountId: Long? = null,
    val accountAdmin: Boolean? = null,
    val salt: String,
    val password: String
)