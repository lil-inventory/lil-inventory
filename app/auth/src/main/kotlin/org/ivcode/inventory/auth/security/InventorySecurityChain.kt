package org.ivcode.inventory.auth.security

import org.springframework.security.core.AuthenticationException
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.filter.OncePerRequestFilter
import javax.servlet.Filter
import javax.servlet.FilterChain
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

/**
 * Defines the inventory security chain
 */
class InventorySecurityChain {

    private val authenticators: MutableList<InventoryAuthenticator> = mutableListOf()

    fun with(authenticator: InventoryAuthenticator): InventorySecurityChain {
        authenticators.add(authenticator)
        return this
    }

    fun build(): Filter = SecurityChainFilter(authenticators)
}

/**
 *
 */
interface InventoryAuthenticator {

    /**
     * Authenticates a user.
     *
     * @return
     *      A fully configured [InventoryAuthentication] object if the is applicable and authentication is successful.
     *      "null" when the filter is not applicable.
     *      An [InventoryAuthenticationException] needs to be thrown if applicable and the authentication fails
     * @throws AuthenticationException if authentication fails
     */
    fun authenticate(request: HttpServletRequest): InventoryAuthentication?
}

/**
 * The servlet [Filter] used in the spring-security mechanism to authenticate users
 */
private class SecurityChainFilter(
    private val authenticators: List<InventoryAuthenticator>
): OncePerRequestFilter() {
    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        // For each authenticator
        for(authenticator in authenticators) {

            // Try to authenticate
            val authentication = try {
                // attempt authentication
                authenticator.authenticate(request)
            } catch (ex: AuthenticationException) {
                // If an exception is thrown, fail the auth and exit
                response.status = HttpServletResponse.SC_UNAUTHORIZED
                return
            }

            // If authenticated, set authority in security context and continue to next filter
            if(authentication!=null) {
                // set authentication
                SecurityContextHolder.getContext().authentication = authentication

                // process request
                filterChain.doFilter(request, response)

                // no post-processing, done
                return
            }
        }

        // If all authenticators failed, 401
        response.status = HttpServletResponse.SC_UNAUTHORIZED
    }
}

