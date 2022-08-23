package org.ivcode.inventory.auth.services.model

data class User (
    val userId: Long,
    val email: String,
    val displayName: String,
    val superAdmin: Boolean,
    val accountId: Long? = null,
    val accountAdmin: Boolean? = null,
)