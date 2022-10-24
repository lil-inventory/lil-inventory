package org.ivcode.inventory.service.model

data class GroupPath (
    val groupId: Long,
    val inventoryId: Long,
    val name: String,
    val path: List<GroupPathElement>
)
