package org.ivcode.inventory.service.model

import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "An asset summary")
data class AssetNavInfo (
    val assetId: Long,
    val name: String
)