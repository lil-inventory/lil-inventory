package org.ivcode.inventory.auth.repositories.model

import java.util.*

data class UserSessionEntity (
    val userSessionId: String,
    val userId: Long,
    val jwtId: String,
    val expiration: Date
)