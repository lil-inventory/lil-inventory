package org.ivcode.inventory.service.model

data class Asset (
    val assetId: Long,
    val inventoryId: Long,
    val name: String,
    val barcode: String? = null,
    val images: List<AssetImageInfo>,
    val group: Group? = null,
)
