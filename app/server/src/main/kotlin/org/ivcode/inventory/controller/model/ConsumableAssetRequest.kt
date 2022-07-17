package org.ivcode.inventory.controller.model

data class ConsumableAssetRequest(
    val inventoryId: Long,
    val name: String,
    val barcode: String?,
    val quantity: Int,
    val quantityMinimum: Int,
    val groupId: Long?
)