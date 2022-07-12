package org.ivcode.inventory.auth.services

import org.ivcode.inventory.auth.repositories.UserDao
import org.ivcode.inventory.auth.repositories.UserPasswordResetDao
import org.ivcode.inventory.auth.repositories.model.UserPasswordResetEntity
import org.ivcode.inventory.auth.utils.createRandomString
import org.ivcode.inventory.auth.utils.hashPassword
import org.ivcode.inventory.common.exception.BadRequest
import org.ivcode.inventory.common.exception.UnauthorizedException
import org.ivcode.inventory.email.service.EmailService
import org.ivcode.inventory.email.service.model.SendPasswordResetRequest
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.*
import java.util.concurrent.TimeUnit

@Service
class UserPasswordResetService(
    private val userDao: UserDao,
    private val userPasswordResetDao: UserPasswordResetDao,
    private val emailService: EmailService,
    private val resetTTL: Long = TimeUnit.HOURS.toMillis(24)
) {

    /**
     * Sends an email to a user to reset their password
     *
     * @param email the user's email
     */
    @Transactional(rollbackFor = [ Throwable::class ])
    fun sendPasswordResetEmail(email: String) {
        val user = userDao.readUserByEmail(email)
        if(true != user?.emailVerified) {
            // if the emails is not verified or null
            throw BadRequest("user's email must be verified to reset password")
        }

        val salt = createRandomString(16)
        val code = createRandomString(32)
        val hash = hashPassword(code, salt)

        val reset = UserPasswordResetEntity(
            userId = user.userId!!,
            salt = salt,
            code = hash,
            expiration = Date(System.currentTimeMillis() + resetTTL)
        )
        userPasswordResetDao.deletePasswordReset(user.userId)
        userPasswordResetDao.createPasswordReset(reset)

        emailService.sendPasswordReset(SendPasswordResetRequest(
            to = email,
            subject = "reset password",
            code = code
        ))
    }

    /**
     * Resets a user's password
     *
     * @param email the user's email address
     * @param code the reset code
     * @param password the new password
     */
    @Transactional(rollbackFor = [ Throwable::class ])
    fun resetPassword (email: String, resetCode: String, newPassword: String) {
        val passwordReset = userPasswordResetDao.readPasswordReset(email) ?: throw UnauthorizedException()
        if (passwordReset.expiration < Date()) {
            // expired
            throw UnauthorizedException()
        }

        val codeHash = hashPassword(resetCode, passwordReset.salt)
        if(codeHash != passwordReset.code) {
            // incorrect code
            throw UnauthorizedException()
        }

        val salt = createRandomString()
        val passwordHash = hashPassword(newPassword, salt)

        userPasswordResetDao.deletePasswordReset(passwordReset.userId)
        userDao.updatePassword(passwordReset.userId, salt, passwordHash)
    }
}