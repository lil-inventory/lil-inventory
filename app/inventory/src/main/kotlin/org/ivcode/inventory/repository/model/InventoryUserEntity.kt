package org.ivcode.inventory.repository.model

data class InventoryUserEntity (
    val inventoryId: Long,
    val userId: Long,
    val read: Boolean,
    val write: Boolean,
    val admin: Boolean
)