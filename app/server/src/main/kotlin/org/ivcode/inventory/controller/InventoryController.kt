package org.ivcode.inventory.controller

import org.ivcode.inventory.auth.security.InventoryAuthentication
import org.ivcode.inventory.service.InventorySecurityService
import org.ivcode.inventory.service.InventoryService
import org.ivcode.inventory.service.model.Inventory
import org.springframework.web.bind.annotation.*
import java.security.Principal

@RestController
@RequestMapping("/inventory")
class InventoryController(
    private val inventoryService: InventoryService,
    private val inventorySecurityService: InventorySecurityService
) {

    @PostMapping
    fun createInventory(
        @RequestParam name: String,
        auth: InventoryAuthentication
    ): Inventory {
        val i = auth.principal.identity
        return inventoryService.createInventory (
            name = name,
            ownerUserId = i.userId
        )
    }

    @GetMapping
    fun getInventories(
        auth: InventoryAuthentication
    ): List<Inventory> = inventoryService.getInventories(auth.principal.identity)

    @PostMapping("/permissions")
    fun setInventoryPermissions(
        @RequestParam inventoryId: Long,
        @RequestParam userEmail: String,
        @RequestParam read: Boolean,
        @RequestParam write: Boolean,
        @RequestParam admin: Boolean,
        auth: InventoryAuthentication,
    ) = inventorySecurityService.setPermissions(
        identity = auth.principal.identity,
        inventoryId = inventoryId,
        email = userEmail,
        read = read,
        write = write,
        admin = admin
    )

    @DeleteMapping("/permissions")
    fun deleteInventoryPermissions (
        @RequestParam inventoryId: Long,
        @RequestParam userEmail: String,
        auth: InventoryAuthentication,
    ) = inventorySecurityService.deletePermissions(
        identity = auth.principal.identity,
        inventoryId = inventoryId,
        email = userEmail
    )
}