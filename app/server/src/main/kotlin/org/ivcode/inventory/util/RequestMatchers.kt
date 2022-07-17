package org.ivcode.inventory.util

import org.springframework.http.HttpMethod
import org.springframework.security.web.util.matcher.AntPathRequestMatcher
import org.springframework.security.web.util.matcher.OrRequestMatcher
import org.springframework.security.web.util.matcher.RequestMatcher

class RequestMatchers {
    companion object {

        fun antMatchers(httpMethod: HttpMethod?, vararg antPatterns: String): RequestMatcher {
            val method = httpMethod?.toString()
            val matchers: MutableList<RequestMatcher> = ArrayList()
            for (pattern in antPatterns) {
                matchers.add(AntPathRequestMatcher(pattern, method))
            }

            return OrRequestMatcher(matchers)
        }

        fun antMatchers(vararg antPatterns: String): RequestMatcher = antMatchers(httpMethod=null, antPatterns=antPatterns)

        fun antMatcher(httpMethod: HttpMethod? = null, antPattern: String = "*"): RequestMatcher = AntPathRequestMatcher(antPattern, httpMethod?.toString())
        fun antMatcher(antPattern: String): RequestMatcher = antMatcher(httpMethod=null, antPattern=antPattern)
    }
}