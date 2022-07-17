package org.ivcode.inventory.service

import org.ivcode.inventory.common.exception.NotFoundException
import org.ivcode.inventory.service.model.NavigationElement
import org.ivcode.inventory.repository.AssetDao
import org.ivcode.inventory.repository.GroupDao
import org.ivcode.inventory.util.parsePath
import org.ivcode.inventory.util.toAssetSummary
import org.ivcode.inventory.util.toGroupSummary
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class GroupNavigationService(
    val assetDao: AssetDao,
    val groupDao: GroupDao
) {

    @Transactional(rollbackFor = [ Throwable::class ])
    fun getRootGroupNavigation(): NavigationElement {
        val groups = groupDao.readGroupsByParent(null)
        val assets = assetDao.readAssetsByGroup(null)

        return NavigationElement(
            groupId = null,
            name = "root",
            path = emptyList(),
            groups = groups.map { it.toGroupSummary() },
            assets = assets.map { it.toAssetSummary() }
        )
    }

    @Transactional(rollbackFor = [ Throwable::class ])
    fun getGroupNavigation(groupInt: Long): NavigationElement {
        val group = groupDao.readGroupPath(groupInt) ?: throw NotFoundException()

        val groups = groupDao.readGroupsByParent(groupInt)
        val assets = assetDao.readAssetsByGroup(groupInt)

        return NavigationElement(
            groupId = group.groupId,
            name = group.name!!,
            path = parsePath(group.path),
            groups = groups.map { it.toGroupSummary() },
            assets = assets.map { it.toAssetSummary() }
        )
    }
}
