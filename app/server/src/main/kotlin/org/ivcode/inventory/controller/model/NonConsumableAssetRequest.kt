package org.ivcode.inventory.controller.model

data class NonConsumableAssetRequest (
    val inventoryId: Long,
    val name: String,
    val barcode: String?,
    val quantity: Int,
    val groupId: Long?,
)