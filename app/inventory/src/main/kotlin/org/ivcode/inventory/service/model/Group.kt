package org.ivcode.inventory.service.model

data class Group (
    val groupId: Long? = null,
    val inventoryId: Long? = null,
    val parentGroupId: Long? = null,
    val name: String? = null
)
