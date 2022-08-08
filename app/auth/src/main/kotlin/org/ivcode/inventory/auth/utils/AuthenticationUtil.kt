package org.ivcode.inventory.auth.utils

import org.ivcode.inventory.auth.security.InventoryAuthentication
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.web.servletapi.SecurityContextHolderAwareRequestWrapper

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
    }
}