package org.ivcode.inventory.util

import com.fasterxml.jackson.databind.ObjectMapper
import org.ivcode.inventory.repository.model.*
import org.ivcode.inventory.service.model.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Configuration
import java.util.*
import javax.annotation.PostConstruct


@Configuration
private class EntityTransformer() {

    @Autowired
    private lateinit var mapper: ObjectMapper

    @PostConstruct
    fun init() {
        objectMapper = mapper
    }
}

private lateinit var objectMapper: ObjectMapper

fun AssetEntity.toAsset(
    quantityTotal: Int? = null,
    group: Group? = null,
    checkouts: List<Checkout>? = null,
    images: List<AssetImageInfo>
) = Asset (
    assetId = this.assetId!!,
    inventoryId = this.inventoryId!!,
    name = this.name!!,
    type = AssetType.findAssetType(this.type!!)!!,
    barcode = this.barcode,
    quantity = this.quantity!!,
    quantityMinimum = this.quantityMinimum,
    quantityTotal = quantityTotal,
    group = group,
    checkouts = checkouts,
    images = images
)

fun AssetEntity.toAssetSummary() = AssetSummary (
    assetId = this.assetId!!,
    type = AssetType.findAssetType(this.type!!)!!,
    name = this.name!!,
    barcode = this.barcode
)

fun GroupEntity.toGroupSummary() = GroupSummary(
    groupId = this.groupId!!,
    name = this.name!!,
    parentGroupId = this.parentGroupId
)

fun GroupPathEntity.toGroup() = Group (
    groupId = this.groupId!!,
    inventoryId = this.inventoryId!!,
    name = this.name!!,
    path = parsePath(this.path)
)

fun CheckoutEntity.toCheckout() = Checkout (
    checkoutId = this.checkoutId!!,
    username = this.userDisplayName!!,
    notes = this.notes,
    timestamp = this.timestamp!!
)

fun ImageInfoEntity.toImageInfo() = ImageInfo (
    imageId = this.imageId!!,
    assetId = this.assetId!!,
    filename = this.filename!!,
    mime = this.mime!!
)

fun ImageInfoEntity.toAssetImageInfo() = AssetImageInfo (
    imageId = this.imageId!!,
    filename = this.filename!!,
    mime = this.mime!!
)

fun InventoryEntity.toInventory() = Inventory (
    inventoryId = this.inventoryId!!,
    name = this.name,
    private =this.private,
    userId = this.userId,
    accountId = this.accountId
)

fun parsePath(path: String?) =
    objectMapper.readValue(path ?: "[]", Array<GroupPathElement>::class.java).asList()