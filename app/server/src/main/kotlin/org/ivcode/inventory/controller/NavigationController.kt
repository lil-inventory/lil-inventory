package org.ivcode.inventory.controller

import org.ivcode.inventory.security.InventoryAuth
import org.ivcode.inventory.service.GroupNavigationService
import org.ivcode.inventory.service.model.NavigationElement
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController


@RestController
@RequestMapping("/inventory/{inventoryId}/navigation")
class NavigationController(
    val groupNavigationService: GroupNavigationService,
    val inventoryAuth: InventoryAuth
) {

    /**
     * Pulls the root navigation element
     */
    @GetMapping
    fun getRoot(
        @PathVariable inventoryId: Long
    ): NavigationElement {
        inventoryAuth.hasRead(inventoryId)
        return groupNavigationService.getRootGroupNavigation(inventoryId)
    }

    /**
     * Pulls the navigation element based on the given group
     */
    @GetMapping("/group/{groupId}")
    fun getGroupNavigation(
        @PathVariable inventoryId: Long,
        @PathVariable groupId: Long
    ): NavigationElement {
        inventoryAuth.hasRead(inventoryId)
        return groupNavigationService.getGroupNavigation(inventoryId,groupId)
    }

    /**
     * Pulls the navigation element based on the given asset
     */
    @GetMapping("/asset/{assetId}")
    fun getAssetNavigation(
        @PathVariable inventoryId: Long,
        @PathVariable assetId: Long
    ): NavigationElement {
        inventoryAuth.hasRead(inventoryId)
        return groupNavigationService.getAssetNavigation(inventoryId, assetId)
    }
}
