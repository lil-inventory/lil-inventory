package org.ivcode.inventory.auth.security.authenticators

import io.jsonwebtoken.JwtException
import org.ivcode.inventory.auth.services.AuthJwtService
import org.ivcode.inventory.auth.security.InventoryAuthentication
import org.ivcode.inventory.auth.security.InventoryAuthenticationException
import org.ivcode.inventory.auth.security.InventoryAuthenticator

abstract class JwtAuthenticator(
    private val authJwtService: AuthJwtService
): InventoryAuthenticator {

    fun parseJwt(token: String): InventoryAuthentication {
        val claims = try {
            authJwtService.getClaims(token)
        } catch (e: JwtException) {
            throw InventoryAuthenticationException("failed to verify jwt")
        }

        return InventoryAuthentication (
            name = authJwtService.email(claims)!!,
            principal = authJwtService.identity(claims)!!,
            credentials = token,
            authenticated = true,
            authorities = emptyList()
        )
    }
}
