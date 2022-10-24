package org.ivcode.inventory.controller

import org.ivcode.inventory.security.InventoryAuth
import org.ivcode.inventory.service.GroupService
import org.ivcode.inventory.service.model.Group
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/inventory/{inventoryId}/group")
class GroupController(
    val groupService: GroupService,
    val inventoryAuth: InventoryAuth
) {

    @PostMapping
    fun createGroup(
        @PathVariable inventoryId: Long,
        @RequestParam name: String,
        @RequestParam parentGroupId: Long?,
    ) {
        inventoryAuth.hasWrite(inventoryId)
        groupService.createGroup(
            inventoryId = inventoryId,
            name = name,
            parentGroupId = parentGroupId
        )
    }

    @GetMapping("/{groupId}")
    fun getGroup(
        @PathVariable inventoryId: Long,
        @PathVariable groupId: Long
    ): Group {
        inventoryAuth.hasRead(inventoryId)
        return groupService.readGroup (
            inventoryId = inventoryId,
            groupId = groupId
        )
    }

    @PutMapping("/{groupId}")
    fun updateGroup(
        @PathVariable inventoryId: Long,
        @PathVariable groupId: Long,
        @RequestParam name: String,
        @RequestParam parentGroupId: Long?,
    ) {
        inventoryAuth.hasWrite(inventoryId)
        groupService.updateGroup(
            inventoryId = inventoryId,
            groupId = groupId,
            updatedName = name,
            updatedParentGroupId = parentGroupId
        )
    }

    @DeleteMapping("/{groupId}")
    fun deleteGroup(
        @PathVariable inventoryId: Long,
        @PathVariable groupId: Long
    ) {
        inventoryAuth.hasWrite(inventoryId)
        groupService.deleteGroup(
            inventoryId = inventoryId,
            groupId = groupId)
    }
}
