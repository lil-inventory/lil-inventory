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

    @Transactional
    fun createGroup(name: String, parentGroupId: Int?): GroupSummary {
        val entity = GroupEntity (
            name = name,
            parentGroupId = parentGroupId
        )

        groupDao.createGroup(entity)
        return entity.toGroupSummary()
    }

    @Transactional
    fun readGroup(groupId: Int): GroupSummary =
        groupDao.readGroup(groupId)?.toGroupSummary() ?: throw NotFoundException()

    @Transactional
    fun updateGroup(groupId: Int, name: String, parentGroupId: Int?): GroupSummary {
        val entity = GroupEntity (
            groupId = groupId,
            name = name,
            parentGroupId = parentGroupId
        )

        val count = groupDao.updateGroup(entity)
        if(count==0) {
            throw NotFoundException()
        }

        return entity.toGroupSummary()
    }

    @Transactional
    fun deleteGroup(groupId: Int) {
        val count = groupDao.deleteGroup(groupId)
        if(count==0) {
            throw NotFoundException()
        }
    }
}