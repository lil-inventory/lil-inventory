package org.ivcode.inventory.service.model

import io.swagger.v3.oas.annotations.media.Schema
import java.time.LocalDateTime

@Schema(description = "Asset checkout information")
data class Checkout(
    @Schema(description = "checkout identifier")
    val checkoutId: Int,

    @Schema(description = "Who checked out the asset")
    val username: String,

    @Schema(description = "Any notes associated with the checkout")
    val notes: String?,

    val timestamp: LocalDateTime
)