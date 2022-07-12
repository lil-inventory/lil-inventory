package org.ivcode.inventory.email.service.model

data class SendEmailVerificationRequest (
    val to: String,
    val subject: String,
    val code: String
)