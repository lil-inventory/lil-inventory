package org.ivcode.inventory.repository.model

import java.time.LocalDateTime

data class CheckoutEntity(
    val checkoutId: Int? = null,
    val assetId: Int? = null,
    val userDisplayName: String? = null,
    val notes: String? = null,
    val timestamp: LocalDateTime? = null
)