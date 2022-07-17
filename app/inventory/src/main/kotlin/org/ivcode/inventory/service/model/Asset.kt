package org.ivcode.inventory.service.model

data class Asset (
    val assetId: Long,
    val inventoryId: Long,
    val name: String,
    val type: AssetType,
    val barcode: String? = null,
    val quantity: Int,
    val quantityMinimum: Int? = null,
    val quantityTotal: Int? = null,
    val checkouts: List<Checkout>? = null,
    val images: List<AssetImageInfo>,
    val group: Group? = null,
)
