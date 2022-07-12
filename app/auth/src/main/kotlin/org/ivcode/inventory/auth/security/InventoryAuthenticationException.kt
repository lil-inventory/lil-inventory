package org.ivcode.inventory.auth.security

import org.springframework.security.core.AuthenticationException

/**
 * An [AuthenticationException] for the inventory security mechanism
 */
class InventoryAuthenticationException(msg: String, cause: Throwable? = null): AuthenticationException(msg, cause)