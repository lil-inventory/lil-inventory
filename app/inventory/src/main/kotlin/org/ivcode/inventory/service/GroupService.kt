package org.ivcode.inventory.service

import org.ivcode.inventory.common.exception.NotFoundException
import org.ivcode.inventory.service.model.GroupSummary
import org.ivcode.inventory.repository.GroupDao
import org.ivcode.inventory.repository.model.GroupEntity
import org.ivcode.inventory.util.toGroupSummary
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class GroupService(
    val groupDao: GroupDao
) {

    @Transactional(rollbackFor = [ Throwable::class ])
    fun createGroup (
        inventoryId: Long,
        name: String,
        parentGroupId: Long?
    ): GroupSummary {
        val entity = GroupEntity (
            inventoryId = inventoryId,
            name = name,
            parentGroupId = parentGroupId
        )

        groupDao.createGroup(entity)
        return entity.toGroupSummary()
    }

    @Transactional(rollbackFor = [ Throwable::class ])
    fun readGroup(
        inventoryId: Long,
        groupId: Long
    ): GroupSummary =
        groupDao.readGroup(inventoryId, groupId)?.toGroupSummary() ?: throw NotFoundException()

    @Transactional(rollbackFor = [ Throwable::class ])
    fun updateGroup(
        inventoryId: Long,
        groupId: Long,
        updatedName: String,
        updatedParentGroupId: Long?) {

        val count = groupDao.updateGroup(
            inventoryId = inventoryId,
            groupId = groupId,
            parentGroupId = updatedParentGroupId,
            name = updatedName
        )
        if(count==0) {
            // TODO count==0 could imply not found or that the inventory ids don't match
            throw NotFoundException()
        }
    }

    @Transactional(rollbackFor = [ Throwable::class ])
    fun deleteGroup (
        inventoryId: Long,
        groupId: Long
    ) {
        val count = groupDao.deleteGroup(inventoryId, groupId)
        if(count==0) {
            throw NotFoundException()
        }
    }
}