package org.ivcode.inventory.repository.model

data class GroupPathEntity (
    val groupId: Int? = null,
    val parentGroupId: Int? = null,
    val name: String? = null,
    val path: String? = null
)