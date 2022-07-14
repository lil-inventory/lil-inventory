package org.ivcode.inventory.controller

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import org.ivcode.inventory.auth.security.InventoryAuthentication
import org.ivcode.inventory.controller.model.CheckoutRequest
import org.ivcode.inventory.controller.model.ConsumableAssetRequest
import org.ivcode.inventory.controller.model.NonConsumableAssetRequest
import org.ivcode.inventory.service.model.Asset
import org.ivcode.inventory.service.model.AssetSummary
import org.ivcode.inventory.service.AssetService
import org.springframework.web.bind.annotation.*
import java.security.Principal

@RestController
@RequestMapping("/assets")
class AssetController (
    private val assetService: AssetService
) {
    @GetMapping("/{assetId}")
    fun getAsset(@PathVariable assetId: Int): Asset =
        assetService.readAsset(assetId)

    @DeleteMapping("/{assetId}")
    fun deleteNonConsumable (@PathVariable assetId: Int) =
        assetService.deleteAsset(assetId)

    @PostMapping("/non-consumable")
    fun createNonConsumable (
        @RequestBody request: NonConsumableAssetRequest
    ): Asset = assetService.createNonConsumableAsset (
        name = request.name,
        barcode = request.barcode,
        quantity = request.quantity,
        groupId = request.groupId
    )

    @PutMapping("/non-consumable/{assetId}")
    fun updateNonConsumable (
        @PathVariable assetId: Int,
        @RequestBody request: NonConsumableAssetRequest
    ): Asset = assetService.updateNonConsumableAsset(
        assetId = assetId,
        name = request.name,
        barcode = request.barcode,
        quantity = request.quantity,
        groupId = request.groupId
    )


    @PostMapping("/non-consumable/{assetId}/checkout")
    @Operation(
        description = "Checkout an asset. This will add a checkout entry to the asset and decrement the available quantity by 1"
    )
    fun checkOutNonConsumable (
        principal: Principal,
        @PathVariable assetId: Int,
        @RequestBody request: CheckoutRequest?
    ): Asset = assetService.checkoutNonConsumableAsset(
        identity = (principal as InventoryAuthentication).principal,
        assetId = assetId,
        notes = request?.notes
    )

    @PostMapping("/non-consumable/{assetId}/check-in")
    @Operation(
        description = "Returns a checked out asset. This is remove the checkout entry from the asset and increment the available quantity by 1"
    )
    fun checkInNonConsumable (
        @PathVariable
        assetId: Int,

        @RequestParam @Parameter(description = "Required if the user has multiple checkouts for the given asset or needs to check-in for another user")
        checkoutId: Int?,

        @RequestParam @Parameter(description = "If true, the checkout entry is removed, but the quantity is not incremented.")
        discard: Boolean = false
    ): Asset = assetService.checkInNonConsumableAsset (
        assetId = assetId,
        checkoutId = checkoutId,
        discard = discard
    )

    @PostMapping("/consumable")
    fun createConsumable(
        @RequestBody request: ConsumableAssetRequest
    ): Asset = assetService.createConsumableAsset(
        name = request.name,
        barcode = request.barcode,
        quantity = request.quantity,
        quantityMinimum = request.quantityMinimum,
        groupId = request.groupId
    )

    @PutMapping("/consumable/{assetId}")
    fun updateConsumable(
        @PathVariable assetId: Int,
        @RequestBody request: ConsumableAssetRequest
    ): Asset = assetService.updateConsumableAsset(
        assetId = assetId,
        name = request.name,
        barcode = request.barcode,
        quantity = request.quantity,
        quantityMinimum = request.quantityMinimum,
        groupId = request.groupId
    )

    @GetMapping("/consumable/restock-list")
    @Operation(
        description = "Returns the consumable assets having a quantity less than the specified minimum quantity"
    )
    fun getRestocks(): List<AssetSummary> {
        return TODO()
    }
}
