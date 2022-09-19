package org.ivcode.inventory.service

import org.ivcode.inventory.common.exception.BadRequestException
import org.ivcode.inventory.common.exception.NotFoundException
import org.ivcode.inventory.service.model.Asset
import org.ivcode.inventory.service.model.AssetType
import org.ivcode.inventory.repository.AssetDao
import org.ivcode.inventory.repository.GroupDao
import org.ivcode.inventory.repository.ImageDao
import org.ivcode.inventory.repository.model.AssetEntity
import org.ivcode.inventory.util.*
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class AssetService(
    private val assetDao: AssetDao,
    private val groupDao: GroupDao,
    private val imageDao: ImageDao
) {

    @Transactional(rollbackFor = [ Throwable::class ])
    fun createNonConsumableAsset (
        inventoryId: Long,
        name: String,
        barcode: String? = null,
        quantity: Int,
        groupId: Long? = null,
    ): Asset = createAsset(AssetEntity(
        inventoryId = inventoryId,
        name = name,
        type = AssetType.NON_CONSUMABLE.code,
        barcode = barcode,
        quantity = quantity,
        groupId = groupId
    ))

    @Transactional(rollbackFor = [ Throwable::class ])
    fun createConsumableAsset(
        inventoryId: Long,
        name: String,
        barcode: String? = null,
        quantity: Int,
        quantityMinimum: Int,
        groupId: Long? = null
    ): Asset = createAsset(AssetEntity(
        inventoryId = inventoryId,
        name = name,
        type = AssetType.CONSUMABLE.code,
        barcode = barcode,
        quantity = quantity,
        quantityMinimum = quantityMinimum,
        groupId = groupId
    ))

    private fun createAsset(assetEntity: AssetEntity): Asset {
        assetDao.createAsset(assetEntity)

        if(assetEntity.assetId == null) {
            // SQL was successful, but insert failed.
            // Cause is most likely because the group's inventory was not equal to the one given
            throw BadRequestException()
        }

        return createAssetDto(assetEntity)
    }

    private fun createAssetDto(assetEntity: AssetEntity): Asset {
        val assetId = assetEntity.assetId!!

        val group = if (assetEntity.groupId!=null) {
            groupDao.readGroupPath(assetEntity.inventoryId!!, assetEntity.groupId)?.toGroup()
        } else {
            null
        }

        val images = imageDao.readAssetImageInfo(assetId).map { it.toAssetImageInfo() }

        val isNonConsumable = assetEntity.type == AssetType.NON_CONSUMABLE.code

        val quantityTotal = if(isNonConsumable) {
            assetEntity.quantity!!
        } else {
            null
        }

        return assetEntity.toAsset (
            quantityTotal = quantityTotal,
            group = group,
            images = images
        )
    }

    @Transactional(rollbackFor = [ Throwable::class ])
    fun readAsset(
        inventoryId: Long,
        assetId: Long
    ): Asset {
        val asset = assetDao.readAsset(inventoryId, assetId) ?: throw NotFoundException()
        return createAssetDto(asset)
    }

    @Transactional(rollbackFor = [ Throwable::class ])
    fun updateNonConsumableAsset(
        assetId: Long,
        inventoryId: Long,
        name: String,
        barcode: String?,
        quantity: Int,
        groupId: Long?
    ): Asset = updateAsset(AssetEntity (
        assetId = assetId,
        inventoryId = inventoryId,
        name = name,
        type = AssetType.NON_CONSUMABLE.code,
        barcode = barcode,
        quantity = quantity,
        groupId = groupId
    ))

    @Transactional(rollbackFor = [ Throwable::class ])
    fun updateConsumableAsset(
        assetId: Long,
        name: String,
        barcode: String? = null,
        quantity: Int,
        quantityMinimum: Int,
        groupId: Long? = null
    ): Asset = updateAsset(AssetEntity(
        assetId = assetId,
        name = name,
        barcode = barcode,
        quantity = quantity,
        quantityMinimum = quantityMinimum,
        groupId = groupId
    ))

    private fun updateAsset(assetEntity: AssetEntity): Asset {
        val count = assetDao.updateAsset(assetEntity)
        if (count==0) {
            // TODO count==0 could imply not found or that the inventory ids don't match
            throw NotFoundException()
        }

        return createAssetDto(assetEntity)
    }

    @Transactional(rollbackFor = [ Throwable::class ])
    fun deleteAsset(
        inventoryId: Long,
        assetId: Long
    ) {
        val count = assetDao.deleteAsset(inventoryId, assetId)
        if(count==0) {
            throw NotFoundException()
        }
    }
}