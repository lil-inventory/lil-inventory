package org.ivcode.inventory.service

import org.ivcode.inventory.auth.services.Account
import org.ivcode.inventory.auth.services.Identity
import org.ivcode.inventory.auth.services.UserService
import org.ivcode.inventory.auth.utils.AuthenticationUtil.Companion.isAccountAdmin
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

    companion object {
        private const val ACCOUNT_DEFAULT_IS_READ = true
        private const val ACCOUNT_DEFAULT_IS_WRITE = false
        private const val ACCOUNT_DEFAULT_IS_ADMIN = false
    }

    @Transactional(rollbackFor = [ Throwable::class ])
    fun getPermissions(
        identity: Identity,
        account: Account?,
        inventoryId: Long
    ): InventorySecurity {
        val inventory = inventoryDao.readInventory(inventoryId) ?: throw NotFoundException()

        return if(inventory.userId==identity.userId) {
            // If we're the owner, then we have all permissions
            InventorySecurity(
                inventory = inventory.toInventory(),
                read = true,
                write = true,
                admin = true,
                owner = true
            )
        } else {
            val isAccountInventory = inventory.userId==null
            if(isAccountInventory && inventory.accountId!=null && inventory.accountId!=account?.accountId) {
                // This is an account level inventory, but the user does not have access to the account
                throw ForbiddenException()
            }

            val isAccountAdmin = isAccountAdmin()
            if(isAccountInventory && inventory.private && !isAccountAdmin) {
                // This is a private account level inventory, and we don't have access
                throw ForbiddenException()
            }

            val inventoryUser = inventoryUserDao.readInventoryUser(inventoryId, identity.userId)

            InventorySecurity(
                inventory = inventory.toInventory(),
                read = getPermission(isAccountInventory, isAccountAdmin, inventoryUser?.read, ACCOUNT_DEFAULT_IS_READ),
                write = getPermission(isAccountInventory, isAccountAdmin, inventoryUser?.write, ACCOUNT_DEFAULT_IS_WRITE),
                admin = getPermission(isAccountInventory, isAccountAdmin, inventoryUser?.admin, ACCOUNT_DEFAULT_IS_ADMIN),
                owner = isAccountAdmin
            )
        }
    }

    private fun getPermission (
        isAccount: Boolean,
        isAccountAdmin: Boolean,
        inventoryUserPermission: Boolean?,
        accountDefaultValue: Boolean
    ): Boolean {
        return if (isAccount) {
            // if an account inventory
            if(isAccountAdmin) true else (inventoryUserPermission ?: accountDefaultValue)
        } else {
            // if a user inventory
            inventoryUserPermission ?: false
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
        account: Account?,
        inventoryId: Long,
        email: String,
        read: Boolean,
        write: Boolean,
        admin: Boolean
    ) {
        val permissions = getPermissions(identity, account, inventoryId)
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
        account: Account?,
        inventoryId: Long,
        email: String,
    ) {
        val permissions = getPermissions(identity, account, inventoryId)
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