package org.ivcode.inventory.auth.security.authenticators

import org.ivcode.inventory.auth.services.AuthJwtService
import org.ivcode.inventory.auth.security.InventoryAuthentication
import javax.servlet.http.HttpServletRequest

class BearerAuthenticator (
    jwtCodec: AuthJwtService
): JwtAuthenticator(jwtCodec) {

    companion object {
        private const val PREFIX = "Bearer "
    }

    override fun authenticate(request: HttpServletRequest): InventoryAuthentication? {
        val authorization = request.getHeader("Authorization") ?: return null

        val token = if(authorization.startsWith(PREFIX, ignoreCase = true)) {
            authorization.substring(PREFIX.length)
        } else {
            return null
        }

        return parseJwt(token)
    }
}
