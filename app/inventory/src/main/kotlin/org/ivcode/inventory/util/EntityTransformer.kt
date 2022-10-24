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
    group: Group? = null,
    images: List<AssetImageInfo>
) = Asset (
    assetId = this.assetId!!,
    inventoryId = this.inventoryId!!,
    name = this.name!!,
    barcode = this.barcode,
    group = group,
    images = images
)

fun AssetEntity.toAssetNavInfo() = AssetNavInfo (
    assetId = this.assetId!!,
    name = this.name!!,
)

fun GroupEntity.toGroup() = Group (
    groupId = this.groupId!!,
    inventoryId = this.inventoryId,
    name = this.name,
    parentGroupId = this.parentGroupId
)

fun GroupEntity.toGroupNavInfo() = GroupNavInfo(
    groupId = this.groupId!!,
    name = this.name!!
)

fun GroupPathEntity.toGroupNavInfo() = GroupNavInfo(
    groupId = this.groupId!!,
    name = this.name!!
)

fun GroupPathEntity.toGroup() = Group (
    groupId = this.groupId!!,
    inventoryId = this.inventoryId!!,
    name = this.name!!
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

fun InventoryEntity.toInventoryNavInfo() = InventoryNavInfo(
    inventoryId = this.inventoryId!!,
    name = this.name
)

fun parsePath(path: String?) =
    objectMapper.readValue(path ?: "[]", Array<GroupPathElement>::class.java).asList()