package org.ivcode.inventory.auth.utils

import org.ivcode.inventory.auth.security.InventoryAuthentication
import org.ivcode.inventory.auth.services.GrantedAuthorities
import org.springframework.security.core.context.SecurityContextHolder

class AuthenticationUtil {
    companion object {

        /**
         * Returns `true` if the current user has an account
         */
        @JvmStatic
        fun hasAccount(): Boolean {
            val auth = SecurityContextHolder.getContext().authentication as InventoryAuthentication?
            return auth?.principal?.account != null
        }

        fun isAccountAdmin(): Boolean {
            val auth = SecurityContextHolder.getContext().authentication as InventoryAuthentication? ?: return false
            val role = auth.authorities.map { it.authority }.firstOrNull { it == GrantedAuthorities.ACCOUNT_ADMIN.roleName }
            return role != null
        }

        fun isSuperAdmin(): Boolean {
            val auth = SecurityContextHolder.getContext().authentication as InventoryAuthentication? ?: return false
            val role = auth.authorities.map { it.authority }.firstOrNull { it == GrantedAuthorities.SUPER_ADMIN.roleName }
            return role != null
        }
    }
}