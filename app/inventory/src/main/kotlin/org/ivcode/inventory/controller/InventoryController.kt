package org.ivcode.inventory.controller

import org.ivcode.inventory.service.InventoryService
import org.ivcode.inventory.service.model.Inventory
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/inventory")
class InventoryController(
    private val inventoryService: InventoryService
) {

    @PostMapping
    fun createInventory(
        @RequestParam name: String
    ): Inventory =
        inventoryService.createInventory(name)

    @GetMapping
    fun getInventories(): List<Inventory> = inventoryService.getInventories()
}