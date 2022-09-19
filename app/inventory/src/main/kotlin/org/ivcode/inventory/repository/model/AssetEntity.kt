package org.ivcode.inventory.repository.model

data class AssetEntity (
    val assetId: Long? = null,
    val inventoryId: Long? = null,
    val name: String? = null,
    val barcode: String? = null,
    val groupId: Long? = null
)
