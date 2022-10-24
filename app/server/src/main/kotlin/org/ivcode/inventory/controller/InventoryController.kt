package org.ivcode.inventory.controller

import org.ivcode.inventory.auth.security.InventoryAuthentication
import org.ivcode.inventory.common.exception.ForbiddenException
import org.ivcode.inventory.security.HasAccount
import org.ivcode.inventory.security.InventoryAuth
import org.ivcode.inventory.service.InventorySecurityService
import org.ivcode.inventory.service.InventoryService
import org.ivcode.inventory.service.model.Inventory
import org.springframework.security.web.servletapi.SecurityContextHolderAwareRequestWrapper
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/inventory")
class InventoryController(
    private val inventoryService: InventoryService,
    private val inventorySecurityService: InventorySecurityService,
    private val inventoryAuth: InventoryAuth
) {

    @HasAccount
    @PostMapping
    fun createAccountInventory(
        auth: InventoryAuthentication,
        w: SecurityContextHolderAwareRequestWrapper,
        @RequestParam name: String,
        @RequestParam private: Boolean = false
    ): Inventory {
        if(private && !w.isUserInRole("ACCOUNT_ADMIN")) {
            // only the account admin can make private account inventories
            throw ForbiddenException()
        }

        return inventoryService.createAccountInventory(
            name = name,
            private = private,
            identity = auth.principal.identity,
            account = auth.principal.account!!
        )
    }

    @HasAccount
    @GetMapping
    fun getAccountInventories (
        auth: InventoryAuthentication
    ): List<Inventory> = inventoryService
        .getAccountInventories(
            identity = auth.principal.identity,
            account = auth.principal.account!!
        )

    @GetMapping("/{inventoryId}")
    fun getInventory (
        @PathVariable inventoryId: Long
    ): Inventory? {
        inventoryAuth.hasRead(inventoryId)
        return inventoryService.getInventory(inventoryId)
    }


    @DeleteMapping("/{inventoryId}")
    fun deleteInventory(
        @PathVariable inventoryId: Long
    ) {
        inventoryAuth.hasWrite(inventoryId)
        return inventoryService.deleteAccountInventory(inventoryId)
    }

    @PostMapping("{inventoryId}/permissions")
    fun setInventoryPermissions(
        @PathVariable inventoryId: Long,
        @RequestParam userEmail: String,
        @RequestParam read: Boolean,
        @RequestParam write: Boolean,
        @RequestParam admin: Boolean,
        auth: InventoryAuthentication,
    ) = inventorySecurityService.setPermissions(
        identity = auth.principal.identity,
        account = auth.principal.account,
        inventoryId = inventoryId,
        email = userEmail,
        read = read,
        write = write,
        admin = admin
    )

    @DeleteMapping("{inventoryId}/permissions")
    fun deleteInventoryPermissions (
        @PathVariable inventoryId: Long,
        @RequestParam userEmail: String,
        auth: InventoryAuthentication,
    ) = inventorySecurityService.deletePermissions(
        identity = auth.principal.identity,
        account = auth.principal.account,
        inventoryId = inventoryId,
        email = userEmail
    )
}