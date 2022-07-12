package org.ivcode.inventory.email.service

import freemarker.template.Configuration
import org.ivcode.inventory.email.service.model.SendEmailVerificationRequest
import org.ivcode.inventory.email.service.model.SendPasswordResetRequest
import org.ivcode.inventory.email.util.Emailer
import org.springframework.stereotype.Service
import java.io.StringWriter

@Service
class EmailService (
    private val emailer: Emailer,
    private val configuration: Configuration
) {

    fun sendEmailValidation(request: SendEmailVerificationRequest) {
        val template = configuration.getTemplate("email-validation.ftl")

        val body = StringWriter().use {
            template.process(mapOf<String, String>(
                "to" to request.to,
                "subject" to request.subject,
                "code" to request.code
            ), it)

            it.flush()
            it.toString()
        }

        emailer.sendEmail (
            to = listOf(request.to),
            subject = request.subject,
            body = body
        )
    }

    fun sendPasswordReset(request: SendPasswordResetRequest) {
        val template = configuration.getTemplate("password-reset.ftl")

        val body = StringWriter().use {
            template.process(mapOf<String, String>(
                "to" to request.to,
                "subject" to request.subject,
                "code" to request.code
            ), it)

            it.flush()
            it.toString()
        }

        emailer.sendEmail (
            to = listOf(request.to),
            subject = request.subject,
            body = body
        )
    }
}