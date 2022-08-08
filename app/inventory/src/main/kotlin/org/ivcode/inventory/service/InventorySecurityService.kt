package org.ivcode.inventory.service

import org.ivcode.inventory.auth.services.Identity
import org.ivcode.inventory.auth.services.UserService
import org.ivcode.inventory.common.exception.BadRequestException
import org.ivcode.inventory.common.exception.ForbiddenException
import org.ivcode.inventory.common.exception.NotFoundException
import org.ivcode.inventory.repository.InventoryDao
import org.ivcode.inventory.repository.InventoryUserDao
import org.ivcode.inventory.repository.model.InventoryUserEntity
import org.ivcode.inventory.service.model.InventorySecurity
import org.ivcode.inventory.util.toInventory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class InventorySecurityService(
    private val inventoryDao: InventoryDao,
    private val inventoryUserDao: InventoryUserDao,
    private val userService: UserService
) {

    @Transactional(rollbackFor = [ Throwable::class ])
    fun getPermissions(
        identity: Identity,
        inventoryId: Long
    ): InventorySecurity {
        val inventory = inventoryDao.readInventory(inventoryId) ?: throw NotFoundException()

        return if(inventory.userId==identity.userId) {
            InventorySecurity(
                inventory = inventory.toInventory(),
                read = true,
                write = true,
                admin = true,
                owner = true
            )
        } else {
            val inventoryUser = inventoryUserDao.readInventoryUser(inventoryId, identity.userId)
            InventorySecurity(
                inventory = inventory.toInventory(),
                read = inventoryUser?.read ?: false,
                write = inventoryUser?.write ?: false,
                admin = inventoryUser?.admin ?: false,
                owner = false
            )
        }
    }

    /**
     * @param identity the current user's identity
     * @param email the email address of the user we're setting permissions for
     * @param inventoryId the inventory id
     * @param read can read?
     * @param write can write?
     * @param admin is admin?
     */
    @Transactional(rollbackFor = [ Throwable::class ])
    fun setPermissions (
        identity: Identity,
        inventoryId: Long,
        email: String,
        read: Boolean,
        write: Boolean,
        admin: Boolean
    ) {
        val permissions = getPermissions(identity, inventoryId)
        if(!permissions.isAdmin()) {
            // Current user lacks admin permissions
            throw ForbiddenException()
        }

        val user = userService.getUser(email)
        if(permissions.inventory.userId==user.userId) {
            // Cannot update owner's permissions
            throw BadRequestException()
        }

        // try update
        val count = inventoryUserDao.updateInventoryUser(
            inventoryId = inventoryId,
            userId = user.userId,
            read = read,
            write = write,
            admin = admin
        )
        if(count==0) {
            // create
            inventoryUserDao.createInventoryUser(
                InventoryUserEntity(
                inventoryId = inventoryId,
                userId = user.userId,
                read = read,
                write = write,
                admin = admin
            ))
        }
    }

    /**
     * @param identity the current user's identity
     * @param email the email address of the user we're setting permissions for
     * @param inventoryId the inventory id
     */
    @Transactional(rollbackFor = [ Throwable::class ])
    fun deletePermissions(
        identity: Identity,
        inventoryId: Long,
        email: String,
    ) {
        val permissions = getPermissions(identity, inventoryId)
        if(!permissions.isAdmin()) {
            // Current user lacks admin permissions
            throw ForbiddenException()
        }

        val user = userService.getUser(email)
        if(permissions.inventory.userId==user.userId) {
            // Cannot update owner's permissions
            throw BadRequestException()
        }

        val count = inventoryUserDao.deleteInventoryUser(inventoryId, user.userId)
        if(count==0) {
            throw NotFoundException()
        }
    }
}