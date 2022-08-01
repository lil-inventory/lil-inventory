package org.ivcode.inventory.auth.repositories.model

data class AccountEntity (
    val accountId: Long? = null,
    val name: String,
    val userCount: Int
)