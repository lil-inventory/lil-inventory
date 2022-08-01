package org.ivcode.inventory.controller

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import org.ivcode.inventory.auth.security.InventoryAuthentication
import org.ivcode.inventory.auth.security.InventoryPrincipal
import org.ivcode.inventory.controller.model.CheckoutRequest
import org.ivcode.inventory.controller.model.ConsumableAssetRequest
import org.ivcode.inventory.controller.model.NonConsumableAssetRequest
import org.ivcode.inventory.security.InventoryAuth
import org.ivcode.inventory.service.model.Asset
import org.ivcode.inventory.service.AssetService
import org.springframework.web.bind.annotation.*
import java.security.Principal

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


    @PostMapping("/non-consumable/{assetId}/checkout")
    @Operation(
        description = "Checkout an asset. This will add a checkout entry to the asset and decrement the available quantity by 1"
    )
    fun checkOutNonConsumable (
        authentication: InventoryAuthentication,
        @PathVariable inventoryId: Long,
        @PathVariable assetId: Long,
        @RequestBody request: CheckoutRequest?
    ) {
        inventoryAuth.hasWrite(inventoryId)
        assetService.checkoutNonConsumableAsset(
            identity = authentication.principal.identity,
            inventoryId = inventoryId,
            assetId = assetId,
            notes = request?.notes
        )
    }

    @PostMapping("/non-consumable/{assetId}/check-in")
    @Operation(
        description = "Returns a checked out asset. This is remove the checkout entry from the asset and increment the available quantity by 1"
    )
    fun checkInNonConsumable (
        @PathVariable inventoryId: Long,
        @PathVariable
        assetId: Long,

        @RequestParam @Parameter(description = "Required if the user has multiple checkouts for the given asset or needs to check-in for another user")
        checkoutId: Long?,

        @RequestParam @Parameter(description = "If true, the checkout entry is removed, but the quantity is not incremented.")
        discard: Boolean = false
    ) {
        inventoryAuth.hasWrite(inventoryId)
        assetService.checkInNonConsumableAsset (
            inventoryId = inventoryId,
            assetId = assetId,
            checkoutId = checkoutId,
            discard = discard
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
