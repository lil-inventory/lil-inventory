package org.ivcode.inventory.repository.model

data class GroupEntity (
    val groupId: Int? = null,
    val parentGroupId: Int? = null,
    val name: String? = null
)
