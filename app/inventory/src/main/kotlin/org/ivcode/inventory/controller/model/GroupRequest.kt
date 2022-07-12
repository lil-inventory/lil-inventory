package org.ivcode.inventory.controller.model

data class GroupRequest(
    val name: String,
    val parentGroupId: Int?
)
