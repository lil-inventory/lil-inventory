package org.ivcode.inventory.auth.security.authenticators

import io.jsonwebtoken.JwtException
import org.ivcode.inventory.auth.services.AuthJwtService
import org.ivcode.inventory.auth.security.InventoryAuthentication
import org.ivcode.inventory.auth.security.InventoryAuthenticationException
import org.ivcode.inventory.auth.security.InventoryAuthenticator
import org.ivcode.inventory.auth.security.InventoryPrincipal
import org.springframework.security.core.authority.SimpleGrantedAuthority

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
            principal = InventoryPrincipal(
                identity = authJwtService.identity(claims)!!,
                account = authJwtService.account(claims)
            ),
            credentials = token,
            authenticated = true,
            authorities = authJwtService.grantedAuthorities(claims)
                ?.map{ SimpleGrantedAuthority(it.roleName) }
                ?: emptyList()
        )
    }
}
