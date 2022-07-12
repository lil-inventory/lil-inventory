package org.ivcode.inventory.service.model

import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "The asset type")
enum class AssetType(val code: String) {
    @Schema(description = "A non-consumable asset is checked out, it's used, and the same asset is checked back in")
    NON_CONSUMABLE("NON_CONSUMABLE"),

    @Schema(description = "A consumable asset is used and discarded. When the quantity goes below the minimum, it needs to be restocked")
    CONSUMABLE("CONSUMABLE")
    ;

    companion object {
        fun findAssetType(code: String) = values().firstOrNull { it.code==code }
    }
}