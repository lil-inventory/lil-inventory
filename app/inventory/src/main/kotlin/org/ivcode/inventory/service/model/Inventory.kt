package org.ivcode.inventory.service.model

data class Inventory(
    val inventoryId: Long,
    val name: String,
    val private: Boolean,
    val userId: Long?,
    val accountId: Long?
)