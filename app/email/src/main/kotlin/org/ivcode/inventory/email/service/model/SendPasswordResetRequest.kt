package org.ivcode.inventory.email.service.model

data class SendPasswordResetRequest (
    val to: String,
    val subject: String,
    val code: String
)