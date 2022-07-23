package org.ivcode.inventory.controller

import org.ivcode.inventory.controller.model.GroupRequest
import org.ivcode.inventory.service.model.GroupSummary
import org.ivcode.inventory.service.GroupService
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/inventory/{inventoryId}/group")
class GroupController(
    val groupService: GroupService
) {

    @PostMapping
    fun createGroup(
        @PathVariable inventoryId: Long,
        @RequestBody request: GroupRequest
    ): GroupSummary = groupService.createGroup(
        inventoryId = request.inventoryId,
        name = request.name,
        parentGroupId = request.parentGroupId
    )

    @GetMapping("/{groupId}")
    fun getGroup(
        @PathVariable inventoryId: Long,
        @PathVariable groupId: Long
    ): GroupSummary =
        groupService.readGroup(groupId)

    @PutMapping("/{groupId}")
    fun updateGroup(
        @PathVariable inventoryId: Long,
        @PathVariable groupId: Long,
        @RequestBody request: GroupRequest
    ): GroupSummary = groupService.updateGroup(
        groupId = groupId,
        inventoryId = request.inventoryId,
        name = request.name,
        parentGroupId =request.parentGroupId
    )

    @DeleteMapping("/{groupId}")
    fun deleteGroup(
        @PathVariable inventoryId: Long,
        @PathVariable groupId: Long
    ) =
        groupService.deleteGroup(groupId)
}
