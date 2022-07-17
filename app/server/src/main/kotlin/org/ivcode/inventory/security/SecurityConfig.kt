package org.ivcode.inventory.security
import org.ivcode.inventory.auth.security.enableInventorySecurity
import org.ivcode.inventory.util.RequestMatchers
import org.springframework.http.HttpMethod.*

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.util.matcher.NegatedRequestMatcher
import org.springframework.security.web.util.matcher.OrRequestMatcher

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
class SecurityConfig {

    @Bean
    fun filterChain(http: HttpSecurity): SecurityFilterChain {

        val permit = OrRequestMatcher(listOf(

            // root redirect
            RequestMatchers.antMatcher("/"),

            // authentication
            RequestMatchers.antMatcher("/auth/*"),

            // swagger
            RequestMatchers.antMatchers("/info", "/swagger-ui/*", "/v3/api-docs", "/v3/api-docs/*"),

            // Http Method: OPTIONS
            RequestMatchers.antMatcher(OPTIONS),
        ))

        http
            .csrf().disable()
            .requestMatcher(NegatedRequestMatcher(permit))
            .enableInventorySecurity()

        return http.build()
    }


}

