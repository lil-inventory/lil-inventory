package org.ivcode.inventory.auth.security

import org.ivcode.inventory.auth.services.Identity
import org.springframework.security.core.Authentication
import org.springframework.security.core.GrantedAuthority

/**
 * The [Authentication] instance for the inventory security mechanism
 */
class InventoryAuthentication(
    private val name: String,
    private val principal: Identity,
    private val credentials: String,
    private var authenticated: Boolean = false,
    private val authorities: Collection<out GrantedAuthority>
): Authentication {
    override fun getName(): String = name
    override fun getAuthorities(): Collection<out GrantedAuthority> = authorities
    override fun getCredentials(): String = credentials
    override fun getDetails(): Any? = null
    override fun getPrincipal(): Identity = principal
    override fun isAuthenticated(): Boolean = this.authenticated
    override fun setAuthenticated(isAuthenticated: Boolean) { this.authenticated = isAuthenticated }
}