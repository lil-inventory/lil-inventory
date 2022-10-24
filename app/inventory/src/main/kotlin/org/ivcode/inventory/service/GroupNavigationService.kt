package org.ivcode.inventory.service

import org.ivcode.inventory.common.exception.NotFoundException
import org.ivcode.inventory.service.model.NavigationElement
import org.ivcode.inventory.repository.AssetDao
import org.ivcode.inventory.repository.GroupDao
import org.ivcode.inventory.repository.InventoryDao
import org.ivcode.inventory.util.*
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class GroupNavigationService(
    val inventoryDao: InventoryDao,
    val groupDao: GroupDao,
    val assetDao: AssetDao
) {

    @Transactional(rollbackFor = [ Throwable::class ])
    fun getRootGroupNavigation(inventoryId: Long): NavigationElement {
        val inventory = inventoryDao.readInventory(inventoryId)
        val groups = groupDao.readGroupsByParent(inventoryId, null)
        val assets = assetDao.readAssetsByGroup(inventoryId, null)

        return NavigationElement(
            inventory = inventory!!.toInventoryNavInfo(),
            group = null,
            path = emptyList(),
            childGroups = groups.map { it.toGroupNavInfo() },
            childAssets = assets.map { it.toAssetNavInfo() }
        )
    }

    @Transactional(rollbackFor = [ Throwable::class ])
    fun getGroupNavigation (
        inventoryId: Long,
        groupInt: Long
    ): NavigationElement {
        val inventory = inventoryDao.readInventory(inventoryId)
        val group = groupDao.readGroupPath(inventoryId, groupInt) ?: throw NotFoundException()

        val groups = groupDao.readGroupsByParent(inventoryId, groupInt)
        val assets = assetDao.readAssetsByGroup(inventoryId, groupInt)

        return NavigationElement(
            inventory = inventory!!.toInventoryNavInfo(),
            group = group.toGroupNavInfo(),
            path = parsePath(group.path),
            childGroups = groups.map { it.toGroupNavInfo() },
            childAssets = assets.map { it.toAssetNavInfo() }
        )
    }

    fun getAssetNavigation (
        inventoryId: Long,
        assetId: Long
    ): NavigationElement {
        val asset = assetDao.readAsset(inventoryId, assetId) ?: throw NotFoundException()

        val groupId = asset.groupId
        return if(groupId==null) {
            getRootGroupNavigation(inventoryId)
        } else {
            getGroupNavigation(inventoryId, groupId)
        }
    }
}
