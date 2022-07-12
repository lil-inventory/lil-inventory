package org.ivcode.inventory.email.conf

import freemarker.cache.ClassTemplateLoader
import freemarker.template.TemplateExceptionHandler
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class FreemarkerConfig {

    @Bean
    fun createFreemarkerConfiguration(): freemarker.template.Configuration {
        val configuration = freemarker.template.Configuration(freemarker.template.Configuration.VERSION_2_3_31)

        configuration.defaultEncoding = "UTF-8";
        configuration.templateExceptionHandler = TemplateExceptionHandler.RETHROW_HANDLER

        val templateLoader = ClassTemplateLoader(FreemarkerConfig::class.java.classLoader, "/templates")
        configuration.templateLoader = templateLoader

        return configuration
    }
}