package org.ivcode.inventory.service

import org.ivcode.inventory.auth.services.Identity
import org.ivcode.inventory.auth.services.UserService
import org.ivcode.inventory.repository.InventoryDao
import org.ivcode.inventory.repository.model.InventoryEntity
import org.ivcode.inventory.service.model.Inventory
import org.ivcode.inventory.util.toInventory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class InventoryService(
    private val inventoryDao: InventoryDao,
    private val userService: UserService
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

    /**
     * @param identity the current user's identity
     * @param email the email address of the user we're setting permissions for
     * @param read can read?
     * @param write can write?
     * @param admin is admin?
     */
    @Transactional(rollbackFor = [ Throwable::class ])
    fun setPermissions(
        identity: Identity,
        email: String,
        read: Boolean,
        write: Boolean,
        admin: Boolean
    ) {
        val user = userService.getUser(email)
        TODO()
    }
}
