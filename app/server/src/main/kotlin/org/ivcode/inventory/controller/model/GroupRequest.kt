package org.ivcode.inventory.controller.model

data class GroupRequest(
    val inventoryId: Long,
    val name: String,
    val parentGroupId: Long?
)
