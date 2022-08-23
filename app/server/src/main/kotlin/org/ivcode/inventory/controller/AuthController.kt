package org.ivcode.inventory.controller

import org.ivcode.inventory.controller.model.UserRequest
import org.ivcode.inventory.auth.services.AuthService
import org.ivcode.inventory.auth.services.UserPasswordResetService
import org.ivcode.inventory.auth.services.model.AccessToken
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/auth")
class AuthController(
    private val authService: AuthService,
    private val passwordResetService: UserPasswordResetService
) {

    @PostMapping("/create")
    fun createSuperAdmin(
        @RequestParam email: String,
        @RequestParam displayName: String,
        @RequestParam password: String,
    ) = authService.createSuperAdmin(
        email = email,
        displayName = displayName,
        password = password
    )

    @PostMapping("/token")
    fun auth(
        @RequestParam(value = "grant_type", required = true) grantType: String,
        @RequestParam(required = false) username: String?,
        @RequestParam(required = false) password: String?,
        @RequestParam(value = "refresh_token", required = false) refreshToken: String?): AccessToken {
        return authService.grantAccess(
            grantType = grantType,
            username = username,
            password = password,
            refreshToken = refreshToken
        )
    }

    @PostMapping("/email")
    fun verifyEmail(
        @RequestParam email: String,
        @RequestParam code: String
    ) {
        authService.verifyEmail(email, code)
    }

    @PostMapping("/password-reset")
    fun sendResetPassword(@RequestParam email: String) {
        passwordResetService.sendPasswordResetEmail(email)
    }

    @PostMapping("/reset-password")
    fun resetPassword(
        @RequestParam email: String,
        @RequestParam code: String,
        @RequestParam password: String
    ) {
        passwordResetService.resetPassword(email, code, password)
    }
}