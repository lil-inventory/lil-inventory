package org.ivcode.inventory.service

import org.ivcode.inventory.auth.services.Account
import org.ivcode.inventory.auth.services.Identity
import org.ivcode.inventory.repository.InventoryDao
import org.ivcode.inventory.repository.InventoryUserDao
import org.ivcode.inventory.repository.model.InventoryEntity
import org.ivcode.inventory.repository.model.InventoryUserEntity
import org.ivcode.inventory.service.model.Inventory
import org.ivcode.inventory.util.toInventory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class InventoryService(
    private val inventoryDao: InventoryDao,
    private val inventoryUserDao: InventoryUserDao
) {

    @Transactional(rollbackFor = [ Throwable::class ])
    fun createAccountInventory (
        identity: Identity,
        account: Account,
        name: String,
        private: Boolean = false
    ): Inventory {
        val entity = InventoryEntity(
            name = name,
            private = private,
            accountId = account.accountId,
            userId = null,
        )
        inventoryDao.createInventory(entity)

        inventoryUserDao.createInventoryUser(InventoryUserEntity(
            inventoryId = entity.inventoryId!!,
            userId = identity.userId,
            read = true,
            write = true,
            admin = true
        ))

        return entity.toInventory()
    }

    @Transactional(rollbackFor = [ Throwable::class ])
    fun getAccountInventories(
        identity: Identity,
        account: Account
    ): List<Inventory> = inventoryDao.readAccountInventories(
        accountId = account.accountId,
        userId = identity.userId
    ).map { it.toInventory() }
}
