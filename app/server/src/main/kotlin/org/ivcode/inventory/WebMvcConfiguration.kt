package org.ivcode.inventory

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Configuration
import org.springframework.web.servlet.config.annotation.CorsRegistry
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry

import org.springframework.web.servlet.config.annotation.WebMvcConfigurer

@Configuration
class WebMvcConfiguration(
    @Value("\${application.home-path}") val homePath: String,
    @Value("\${cors.allowed-origins}") val allowedOrigins: List<String>,
    @Value("\${cors.allow-credentials}") val allowCredentials: Boolean,
): WebMvcConfigurer {

    override fun addViewControllers(registry: ViewControllerRegistry) =
        // redirect root requests to the swagger-ui
        registry.addViewController("/").setViewName("redirect:${homePath}")

    override fun addCorsMappings(registry: CorsRegistry) {
        // TODO move CORS to properties
        registry
            .addMapping("/**")
            .allowedOrigins (*allowedOrigins.toTypedArray())
            .allowCredentials(allowCredentials)
    }
}
