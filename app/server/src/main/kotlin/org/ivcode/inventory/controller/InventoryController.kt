package org.ivcode.inventory.controller

import org.ivcode.inventory.auth.security.InventoryAuthentication
import org.ivcode.inventory.service.InventoryService
import org.ivcode.inventory.service.model.Inventory
import org.springframework.web.bind.annotation.*
import java.security.Principal

@RestController
@RequestMapping("/inventory")
class InventoryController(
    private val inventoryService: InventoryService
) {

    @PostMapping
    fun createInventory(
        @RequestParam name: String,
        auth: InventoryAuthentication
    ): Inventory {
        val i = auth.principal
        return inventoryService.createInventory (
            name = name,
            ownerUserId = i.userId
        )
    }

    @GetMapping
    fun getInventories(): List<Inventory> = inventoryService.getInventories()
}