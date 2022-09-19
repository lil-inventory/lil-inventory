package org.ivcode.inventory.controller.model

data class AssetRequest(
    val name: String,
    val barcode: String?,
    val groupId: Long?
)