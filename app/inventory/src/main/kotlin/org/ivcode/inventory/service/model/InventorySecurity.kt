package org.ivcode.inventory.service.model

data class InventorySecurity (
    val inventory: Inventory,
    private val read: Boolean,
    private val write: Boolean,
    private val admin: Boolean,
    private val owner: Boolean
) {
    fun isRead() = read || admin || owner
    fun isWrite() = write || admin || owner
    fun isAdmin() = admin || owner
    fun isOwner() = owner
}