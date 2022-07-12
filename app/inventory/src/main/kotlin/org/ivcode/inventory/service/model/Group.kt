package org.ivcode.inventory.service.model

data class Group(
    val groupId: Int,
    val name: String,
    val path: List<GroupPathElement>
)
