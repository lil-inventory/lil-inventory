package org.ivcode.inventory.auth.security.authenticators

import org.ivcode.inventory.auth.services.AuthJwtService
import org.ivcode.inventory.auth.security.InventoryAuthentication
import javax.servlet.http.HttpServletRequest

class CookieAuthenticator(
    jwtCodec: AuthJwtService
): JwtAuthenticator(jwtCodec) {

    companion object {
        private const val COOKIE_NAME = "INVENTORY_TOKEN"
    }

    override fun authenticate(request: HttpServletRequest): InventoryAuthentication? {
        val cookie = request.cookies
            ?.firstOrNull { it.name.equals(COOKIE_NAME, ignoreCase = true) }?.value
            ?: return null


        return parseJwt(cookie)
    }
}