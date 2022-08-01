package org.ivcode.inventory.security

import org.ivcode.inventory.auth.security.InventoryAuthentication
import org.ivcode.inventory.common.exception.ForbiddenException
import org.ivcode.inventory.service.InventorySecurityService
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Component

@Component
class InventoryAuth (
    private val inventorySecurityService: InventorySecurityService
) {
    fun hasRead (inventoryId: Long) = has(inventoryId, read = true)
    fun hasWrite (inventoryId: Long) = has(inventoryId, write = true)
    fun hasAdmin (inventoryId: Long) = has(inventoryId, admin = true)
    fun hasOwner (inventoryId: Long) = has(inventoryId, owner = true)

    fun has (
        inventoryId: Long,
        read: Boolean=false,
        write: Boolean=false,
        admin: Boolean=false,
        owner: Boolean=false
    ) {
        val auth = (SecurityContextHolder.getContext().authentication ?: throw ForbiddenException()) as InventoryAuthentication
        val inventorySecurity = inventorySecurityService.getPermissions(auth.principal.identity, inventoryId)

        if(read && !inventorySecurity.isRead()) {
            throw ForbiddenException()
        }
        if(write && !inventorySecurity.isWrite()) {
            throw ForbiddenException()
        }
        if(admin && !inventorySecurity.isAdmin()) {
            throw ForbiddenException()
        }
        if(owner && !inventorySecurity.isOwner()) {
            throw ForbiddenException()
        }
    }
}