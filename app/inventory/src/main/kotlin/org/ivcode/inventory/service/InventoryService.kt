package org.ivcode.inventory.service

import org.ivcode.inventory.repository.InventoryDao
import org.ivcode.inventory.repository.model.InventoryEntity
import org.ivcode.inventory.service.model.Inventory
import org.ivcode.inventory.util.toInventory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class InventoryService(
    private val inventoryDao: InventoryDao,
) {

    @Transactional(rollbackFor = [ Throwable::class ])
    fun createInventory(
        name: String,
        ownerUserId: Long
    ): Inventory {
        val entity = InventoryEntity(
            name = name,
            ownerUserId = ownerUserId
        )
        inventoryDao.createInventory(entity)

        return entity.toInventory()
    }

    @Transactional(rollbackFor = [ Throwable::class ])
    fun getInventories(): List<Inventory> {
        return inventoryDao.readInventories()?.map { it.toInventory() }
    }
}
