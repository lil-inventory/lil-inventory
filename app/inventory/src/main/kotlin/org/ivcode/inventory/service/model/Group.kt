package org.ivcode.inventory.service.model

data class Group(
    val groupId: Long,
    val inventoryId: Long,
    val name: String,
    val path: List<GroupPathElement>
)
