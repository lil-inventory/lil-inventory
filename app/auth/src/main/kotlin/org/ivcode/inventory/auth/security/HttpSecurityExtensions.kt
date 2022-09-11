package org.ivcode.inventory.auth.security

import org.ivcode.inventory.auth.services.AuthJwtService
import org.ivcode.inventory.auth.security.authenticators.BearerAuthenticator
import org.ivcode.inventory.auth.security.authenticators.CookieAuthenticator
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.web.csrf.CsrfFilter
import javax.annotation.PostConstruct

@Configuration
private class HttpSecurityExtensionsConfig() {

    @Autowired
    private lateinit var _jwtCodec: AuthJwtService

    @PostConstruct
    fun init() {
        jwtCodec = _jwtCodec
    }
}

private lateinit var jwtCodec: AuthJwtService


/**
 * Enables the inventory security mechanism.
 */
fun HttpSecurity.enableInventorySecurity(): HttpSecurity = this
    .sessionManagement()
        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
        .and()
    .cors()
        .and()
    .addFilterBefore(InventorySecurityChain()
        .with(BearerAuthenticator(jwtCodec))
        .with(CookieAuthenticator(jwtCodec))
        .build(), CsrfFilter::class.java)
