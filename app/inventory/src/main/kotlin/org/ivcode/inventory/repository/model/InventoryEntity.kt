package org.ivcode.inventory.repository.model

data class InventoryEntity(
    val inventoryId: Long? = null,
    val name: String,
    val private: Boolean,
    val accountId: Long?,
    val userId: Long?
)