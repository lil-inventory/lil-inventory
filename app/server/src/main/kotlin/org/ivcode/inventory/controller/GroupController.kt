package org.ivcode.inventory.controller

import org.ivcode.inventory.controller.model.GroupRequest
import org.ivcode.inventory.service.model.GroupSummary
import org.ivcode.inventory.service.GroupService
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/group")
class GroupController(
    val groupService: GroupService
) {

    @PostMapping
    fun createGroup(@RequestBody request: GroupRequest): GroupSummary = groupService.createGroup(
        name = request.name,
        parentGroupId = request.parentGroupId
    )

    @GetMapping("/{groupId}")
    fun getGroup(@PathVariable groupId: Int): GroupSummary =
        groupService.readGroup(groupId)

    @PutMapping("/{groupId}")
    fun updateGroup(
        @PathVariable groupId: Int,
        @RequestBody request: GroupRequest
    ): GroupSummary = groupService.updateGroup(
        groupId = groupId,
        name = request.name,
        parentGroupId =request.parentGroupId
    )

    @DeleteMapping("/{groupId}")
    fun deleteGroup(@PathVariable groupId: Int) =
        groupService.deleteGroup(groupId)
}
