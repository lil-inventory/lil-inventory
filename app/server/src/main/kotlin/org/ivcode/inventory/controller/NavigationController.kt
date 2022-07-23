package org.ivcode.inventory.controller

import org.ivcode.inventory.service.GroupNavigationService
import org.ivcode.inventory.service.model.NavigationElement
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController


@RestController
@RequestMapping("/inventory/{inventoryId}/navigation")
class NavigationController(
    val groupNavigationService: GroupNavigationService
) {

    @GetMapping("/")
    fun getRoot(
        @PathVariable inventoryId: Long
    ): NavigationElement {
        return groupNavigationService.getRootGroupNavigation(inventoryId)
    }

    @GetMapping("/{groupId}")
    fun getGroupNavigation(
        @PathVariable inventoryId: Long,
        @PathVariable groupId: Long
    ): NavigationElement =
        groupNavigationService.getGroupNavigation(inventoryId,groupId)

}
