package org.ivcode.inventory.email.util

import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import java.util.*
import javax.mail.Message
import javax.mail.Session
import javax.mail.Transport
import javax.mail.internet.InternetAddress
import javax.mail.internet.MimeMessage

@Component
class Emailer (
    @Value("\${smtp.host}") private val smtpHost: String,
    @Value("\${smtp.port}") private val smtpPort: Int,
    @Value("\${smtp.from}") private val smtpFrom: String
) {

    companion object {
        val CHARSET: String = Charsets.UTF_8.toString()
    }

    fun sendEmail (
        from: String = smtpFrom,
        to: List<String>,
        cc: List<String>? = null,
        bcc: List<String>? = null,
        subject: String,
        body: String
    ) {
        val properties = Properties().apply {
            setProperty("mail.smtp.host", smtpHost)
            setProperty("mail.smtp.port", smtpPort.toString())
        }
        val session = Session.getInstance(properties)

        val msg = MimeMessage(session).apply {
            addHeader("Content-type", "text/HTML; charset=$CHARSET");
            addHeader("format", "flowed");
            addHeader("Content-Transfer-Encoding", "8bit");

            setFrom(InternetAddress(from))

            setRecipients(Message.RecipientType.TO, to.map{InternetAddress(it)}.toTypedArray())
            if(cc!=null && cc.isNotEmpty()) {
                setRecipients(Message.RecipientType.CC, cc.map { InternetAddress(it) }.toTypedArray())
            }
            if(bcc!=null && bcc.isNotEmpty()) {
                setRecipients(Message.RecipientType.BCC, bcc.map { InternetAddress(it) }.toTypedArray())
            }

            setSubject(subject, CHARSET)
            sentDate = Date()

            setContent(body, "text/html")
        }

        Transport.send(msg)
    }
}