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
    fun createAsset (
        inventoryId: Long,
        name: String,
        barcode: String? = null,
        groupId: Long? = null
    ) = createAsset(AssetEntity(
        inventoryId = inventoryId,
        name = name,
        barcode = barcode,
        groupId = groupId
    ))

    private fun createAsset(assetEntity: AssetEntity) {
        assetDao.createAsset(assetEntity)

        if(assetEntity.assetId == null) {
            // SQL was successful, but insert failed.
            // Cause is most likely because the group's inventory was not equal to the one given
            throw BadRequestException()
        }
    }

    private fun createAssetDto(assetEntity: AssetEntity): Asset {
        val assetId = assetEntity.assetId!!

        val group = if (assetEntity.groupId!=null) {
            groupDao.readGroupPath(assetEntity.inventoryId!!, assetEntity.groupId)?.toGroup()
        } else {
            null
        }

        val images = imageDao.readAssetImageInfo(assetId).map { it.toAssetImageInfo() }

        return assetEntity.toAsset (
            group = group,
            images = images
        )
    }

    @Transactional(rollbackFor = [ Throwable::class ])
    fun readAsset (
        inventoryId: Long,
        assetId: Long
    ): Asset {
        val asset = assetDao.readAsset(inventoryId, assetId) ?: throw NotFoundException()
        return createAssetDto(asset)
    }

    @Transactional(rollbackFor = [ Throwable::class ])
    fun updateAsset(
        assetId: Long,
        name: String,
        barcode: String? = null,
        groupId: Long? = null
    ) = updateAsset(AssetEntity(
        assetId = assetId,
        name = name,
        barcode = barcode,
        groupId = groupId
    ))

    private fun updateAsset(assetEntity: AssetEntity) {
        val count = assetDao.updateAsset(assetEntity)
        if (count==0) {
            // TODO count==0 could imply not found or that the inventory ids don't match
            throw NotFoundException()
        }
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