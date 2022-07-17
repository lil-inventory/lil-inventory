package org.ivcode.inventory.repository.model

data class GroupEntity (
    val groupId: Long? = null,
    val inventoryId: Long? = null,
    val parentGroupId: Long? = null,
    val name: String? = null
)
