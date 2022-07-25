package org.ivcode.inventory.auth.utils

import org.ivcode.inventory.auth.services.Identity
import org.ivcode.inventory.auth.services.TokenInfo
import org.ivcode.inventory.auth.repositories.model.UserEntity
import org.ivcode.inventory.auth.services.model.AccessToken
import org.ivcode.inventory.auth.services.model.User

internal fun UserEntity.toIdentity() = Identity (
    userId = this.userId!!,
    email = this.email,
    emailVerified = this.emailVerified,
    displayName = this.displayName
)

internal fun UserEntity.toUser() = User (
    userId = this.userId!!,
    email = this.email,
    displayName = this.displayName
)

internal fun TokenInfo.toAccessToken() = AccessToken (
    accessToken = this.access.token,
    expiresIn = this.access.expiration.time - System.currentTimeMillis(),
    refreshToken = this.refresh.token,
)