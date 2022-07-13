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
import org.ivcode.inventory.auth.services.AuthService
import org.ivcode.inventory.auth.services.Identity
import org.ivcode.inventory.util.*
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class AssetService(
    private val assetDao: AssetDao,
    private val checkoutDao: CheckoutDao,
    private val groupDao: GroupDao,
    private val imageDao: ImageDao,
) {

    @Transactional
    fun createNonConsumableAsset (
        name: String,
        barcode: String? = null,
        quantity: Int,
        groupId: Int? = null,
    ): Asset = createAsset(AssetEntity(
        name = name,
        type = AssetType.NON_CONSUMABLE.code,
        barcode = barcode,
        quantity = quantity,
        groupId = groupId
    ))

    @Transactional
    fun createConsumableAsset(
        name: String,
        barcode: String? = null,
        quantity: Int,
        quantityMinimum: Int,
        groupId: Int? = null
    ): Asset = createAsset(AssetEntity(
        name = name,
        type = AssetType.CONSUMABLE.code,
        barcode = barcode,
        quantity = quantity,
        quantityMinimum = quantityMinimum,
        groupId = groupId
    ))

    private fun createAsset(assetEntity: AssetEntity): Asset {
        assetDao.createAsset(assetEntity)
        return createAssetDto(assetEntity)
    }

    private fun createAssetDto(assetEntity: AssetEntity): Asset {
        val assetId = assetEntity.assetId!!

        val group = if (assetEntity.groupId!=null) {
            groupDao.readGroupPath(assetEntity.groupId)?.toGroup()
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

    @Transactional
    fun readAsset(assetId: Int): Asset {
        val asset = assetDao.readAsset(assetId) ?: throw NotFoundException()
        return createAssetDto(asset)
    }

    @Transactional
    fun updateNonConsumableAsset(
        assetId: Int,
        name: String,
        barcode: String?,
        quantity: Int,
        groupId: Int?
    ): Asset = updateAsset(AssetEntity (
        assetId = assetId,
        name = name,
        type = AssetType.NON_CONSUMABLE.code,
        barcode = barcode,
        quantity = quantity,
        groupId = groupId
    ))

    @Transactional
    fun updateConsumableAsset(
        assetId: Int,
        name: String,
        barcode: String? = null,
        quantity: Int,
        quantityMinimum: Int,
        groupId: Int? = null
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
            throw NotFoundException()
        }

        return createAssetDto(assetEntity)
    }

    @Transactional
    fun deleteAsset(assetId: Int) {
        val count = assetDao.deleteAsset(assetId)
        if(count==0) {
            throw NotFoundException()
        }
    }

    @Transactional
    fun checkoutNonConsumableAsset (
        identity: Identity,
        assetId: Int,
        notes: String?
    ): Asset {
        assetDao.addQuantity(assetId, -1)
        checkoutDao.createCheckout(assetId, identity.userId, notes)

        return readAsset(assetId)
    }

    @Transactional
    fun checkInNonConsumableAsset(
        assetId: Int,
        checkoutId: Int?,
        discard: Boolean = false
    ): Asset {
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
        val finalCheckoutId = checkoutId ?: if(userCheckoutIds.size==1) {
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
            assetDao.addQuantity(assetId, 1)
        }
        checkoutDao.deleteCheckouts(finalCheckoutId)

        return readAsset(assetId)
    }
}