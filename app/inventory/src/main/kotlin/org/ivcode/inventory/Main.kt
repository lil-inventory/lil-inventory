package org.ivcode.inventory

import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.autoconfigure.security.servlet.UserDetailsServiceAutoConfiguration
import org.springframework.transaction.annotation.EnableTransactionManagement

@SpringBootApplication(exclude = [UserDetailsServiceAutoConfiguration::class])
@EnableTransactionManagement
class Main

fun main(args: Array<String>) {
    SpringApplication.run(Main::class.java, *args)
}
