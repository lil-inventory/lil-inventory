package org.ivcode.inventory.controller

import org.ivcode.inventory.auth.security.InventoryAuthentication
import org.ivcode.inventory.auth.services.AccountService
import org.ivcode.inventory.auth.services.AuthService
import org.ivcode.inventory.auth.services.model.User
import org.ivcode.inventory.security.AccountAdmin
import org.ivcode.inventory.security.SuperAdmin
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/account")
class AccountController(
    private val accountService: AccountService
) {

    @SuperAdmin
    @PostMapping
    fun createAccount(
        @RequestParam accountName: String,
        @RequestParam userCount: Int,
        @RequestParam adminEmail: String,
        @RequestParam adminDisplayName: String,
        @RequestParam adminPassword: String
    ) = accountService.createAccount (
        accountName = accountName,
        userCount = userCount,
        adminEmail = adminEmail,
        adminDisplayName = adminDisplayName,
        adminPassword = adminPassword
    )

    @AccountAdmin
    @GetMapping("/users")
    fun getAccountUsers(
        auth: InventoryAuthentication,
    ): List<User> = accountService.getUsers(
        accountId = auth.principal.account!!.accountId
    )

    @AccountAdmin
    @PostMapping("/user")
    fun createAccountUser (
        auth: InventoryAuthentication,
        @RequestParam email: String,
        @RequestParam displayName: String,
        @RequestParam password: String,
    ) = accountService.createUser(
        accountId = auth.principal.account!!.accountId,
        email = email,
        displayName = displayName,
        password = password
    )

    @AccountAdmin
    @DeleteMapping("/user")
    fun deleteAccountUser(
        auth: InventoryAuthentication,
        @RequestParam userId: Long,
    ) = accountService.deleteUser (
        accountId = auth.principal.account!!.accountId,
        currentUserId = auth.principal.identity.userId,
        deleteUserId = userId
    )
}