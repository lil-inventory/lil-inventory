package org.ivcode.inventory.controller

import org.ivcode.inventory.controller.model.AssetRequest
import org.ivcode.inventory.security.InventoryAuth
import org.ivcode.inventory.service.model.Asset
import org.ivcode.inventory.service.AssetService
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/inventory/{inventoryId}/assets")
class AssetController (
    private val assetService: AssetService,
    private val inventoryAuth: InventoryAuth
) {
    @GetMapping("/{assetId}")
    fun getAsset(
        @PathVariable inventoryId: Long,
        @PathVariable assetId: Long
    ): Asset {
        inventoryAuth.hasRead(inventoryId)
        return assetService.readAsset(
            inventoryId = inventoryId,
            assetId = assetId
        )
    }

    @DeleteMapping("/{assetId}")
    fun deleteNonConsumable (
        @PathVariable inventoryId: Long,
        @PathVariable assetId: Long
    ) {
        inventoryAuth.hasWrite(inventoryId)
        assetService.deleteAsset(
            inventoryId = inventoryId,
            assetId = assetId
        )
    }

    @PostMapping
    fun createAsset(
        @PathVariable inventoryId: Long,
        @RequestBody request: AssetRequest
    ) {
        inventoryAuth.hasWrite(inventoryId)
        return assetService.createAsset(
            inventoryId = inventoryId,
            name = request.name,
            barcode = request.barcode,
            groupId = request.groupId
        )
    }

    @PutMapping("/{assetId}")
    fun updateAsset(
        @PathVariable inventoryId: Long,
        @PathVariable assetId: Long,
        @RequestBody request: AssetRequest
    ) {
        inventoryAuth.hasWrite(inventoryId)
        return assetService.updateAsset(
            assetId = assetId,
            name = request.name,
            barcode = request.barcode,
            groupId = request.groupId
        )
    }
}
