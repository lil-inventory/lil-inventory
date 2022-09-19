package org.ivcode.inventory.controller

import org.ivcode.inventory.controller.model.ConsumableAssetRequest
import org.ivcode.inventory.controller.model.NonConsumableAssetRequest
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

    @PostMapping("/non-consumable")
    fun createNonConsumable (
        @PathVariable inventoryId: Long,
        @RequestBody request: NonConsumableAssetRequest
    ): Asset {
        inventoryAuth.hasWrite(inventoryId)
        return assetService.createNonConsumableAsset (
            inventoryId = inventoryId,
            name = request.name,
            barcode = request.barcode,
            quantity = request.quantity,
            groupId = request.groupId
        )
    }

    @PutMapping("/non-consumable/{assetId}")
    fun updateNonConsumable (
        @PathVariable inventoryId: Long,
        @PathVariable assetId: Long,
        @RequestBody request: NonConsumableAssetRequest
    ): Asset {
        inventoryAuth.hasWrite(inventoryId)
        return assetService.updateNonConsumableAsset(
            assetId = assetId,
            inventoryId = inventoryId,
            name = request.name,
            barcode = request.barcode,
            quantity = request.quantity,
            groupId = request.groupId
        )
    }

    @PostMapping("/consumable")
    fun createConsumable(
        @PathVariable inventoryId: Long,
        @RequestBody request: ConsumableAssetRequest
    ): Asset {
        inventoryAuth.hasWrite(inventoryId)
        return assetService.createConsumableAsset(
            inventoryId = inventoryId,
            name = request.name,
            barcode = request.barcode,
            quantity = request.quantity,
            quantityMinimum = request.quantityMinimum,
            groupId = request.groupId
        )
    }

    @PutMapping("/consumable/{assetId}")
    fun updateConsumable(
        @PathVariable inventoryId: Long,
        @PathVariable assetId: Long,
        @RequestBody request: ConsumableAssetRequest
    ): Asset {
        inventoryAuth.hasWrite(inventoryId)
        return assetService.updateConsumableAsset(
            assetId = assetId,
            name = request.name,
            barcode = request.barcode,
            quantity = request.quantity,
            quantityMinimum = request.quantityMinimum,
            groupId = request.groupId
        )
    }
}
