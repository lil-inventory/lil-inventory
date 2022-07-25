package org.ivcode.inventory.service

import org.ivcode.inventory.common.exception.BadRequest
import org.ivcode.inventory.common.exception.NotFoundException
import org.ivcode.inventory.service.model.Asset
import org.ivcode.inventory.service.model.AssetType
import org.ivcode.inventory.repository.AssetDao
import org.ivcode.inventory.repository.CheckoutDao
import org.ivcode.inventory.repository.GroupDao
import org.ivcode.inventory.repository.ImageDao
import org.ivcode.inventory.repository.model.AssetEntity
import org.ivcode.inventory.auth.services.Identity
import org.ivcode.inventory.util.*
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class AssetService(
    private val assetDao: AssetDao,
    private val checkoutDao: CheckoutDao,
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
            throw BadRequest()
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

        val checkouts = if(isNonConsumable) {
            checkoutDao.readCheckouts(assetId).map { it.toCheckout() }
        } else {
            null
        }

        val quantityTotal = if(isNonConsumable) {
            assetEntity.quantity!! + (checkouts?.size ?: 0)
        } else {
            null
        }

        return assetEntity.toAsset (
            quantityTotal = quantityTotal,
            group = group,
            checkouts = checkouts,
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

    @Transactional(rollbackFor = [ Throwable::class ])
    fun checkoutNonConsumableAsset (
        identity: Identity,
        inventoryId: Long,
        assetId: Long,
        notes: String?
    ) {
        assetDao.addQuantity(inventoryId, assetId, -1)
        checkoutDao.createCheckout(assetId, identity.userId, notes)
    }

    @Transactional(rollbackFor = [ Throwable::class ])
    fun checkInNonConsumableAsset(
        assetId: Long,
        inventoryId: Long,
        checkoutId: Long?,
        discard: Boolean = false
    ) {
        val checkouts = checkoutDao.readCheckouts(assetId)

        // Make sure there are checkouts to delete
        if(checkouts.isEmpty()) {
            throw NotFoundException("asset has no checkouts")
        }

        // All checkout ids
        val checkoutIds = checkouts.map { it.checkoutId }.toSet()

        // TODO filter to checkouts made by the user
        // User specific checkout ids
        val userCheckoutIds = checkoutIds

        // A checkout id is required if more than one of the asset is checkout by the user
        val finalCheckoutId: Long = checkoutId ?: if(userCheckoutIds.size==1) {
            // checkout id not found, and only one user checkout defined
            checkoutIds.first()!!
        } else if(userCheckoutIds.isEmpty()) {
            // checkout id not found, and no user checkout defined
            throw NotFoundException("no checkouts for user found")
        } else {
            // checkout id not found, and multiple user checkout defined
            throw BadRequest("checkout id not defined but multiple for user were found")
        }

        // Make sure the given checkout id exists within the asset
        if(!checkoutIds.contains(finalCheckoutId)) {
            throw BadRequest("checkout id not found within asset")
        }

        if(!discard) {
            assetDao.addQuantity(inventoryId, assetId, 1)
        }
        checkoutDao.deleteCheckouts(finalCheckoutId)
    }
}