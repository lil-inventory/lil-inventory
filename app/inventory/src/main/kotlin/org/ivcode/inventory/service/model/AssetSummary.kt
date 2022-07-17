package org.ivcode.inventory.service.model

import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "An asset summary")
data class AssetSummary (
    val assetId: Long,
    val type: AssetType,
    val name: String,
    val barcode: String?
)