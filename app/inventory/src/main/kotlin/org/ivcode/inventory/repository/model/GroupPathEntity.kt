package org.ivcode.inventory.repository.model

data class GroupPathEntity (
    val groupId: Long? = null,
    val inventoryId: Long? = null,
    val parentGroupId: Long? = null,
    val name: String? = null,
    val path: String? = null
)