package org.ivcode.inventory.auth.services

import org.ivcode.inventory.auth.repositories.AccountDao
import org.ivcode.inventory.auth.repositories.UserDao
import org.ivcode.inventory.auth.repositories.UserEmailVerificationDao
import org.ivcode.inventory.auth.repositories.model.AccountEntity
import org.ivcode.inventory.auth.repositories.model.UserEmailVerificationEntity
import org.ivcode.inventory.auth.repositories.model.UserEntity
import org.ivcode.inventory.auth.services.model.User
import org.ivcode.inventory.auth.utils.createRandomString
import org.ivcode.inventory.auth.utils.hashPassword
import org.ivcode.inventory.auth.utils.toUser
import org.ivcode.inventory.common.exception.BadRequest
import org.ivcode.inventory.common.exception.ForbiddenException
import org.ivcode.inventory.common.exception.NotFoundException
import org.ivcode.inventory.email.service.EmailService
import org.ivcode.inventory.email.service.model.SendEmailVerificationRequest
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class AccountService(
    private val userDao: UserDao,
    private val emailVerificationDao: UserEmailVerificationDao,
    private val accountDao: AccountDao,
    private val emailService: EmailService
) {

    @Transactional(rollbackFor = [ Throwable::class ])
    fun createAccount(
        accountName: String,
        userCount: Int,
        adminEmail: String,
        adminDisplayName: String,
        adminPassword: String) {

        val account = AccountEntity(
            name = accountName,
            userCount = userCount
        )
        accountDao.createAccount(account)


        // Setup User
        val salt = createRandomString(16)
        val hash = hashPassword(adminPassword, salt)

        val user = UserEntity (
            email = adminEmail,
            emailVerified = false,
            displayName = adminDisplayName,
            superAdmin = false,
            accountId = account!!.accountId,
            accountAdmin = true,
            salt = salt,
            password = hash
        )
        userDao.createUser(user)

        val emailSalt = createRandomString(16)
        val emailCode = createRandomString(32)
        val emailCodeHash = hashPassword(emailCode, emailSalt)

        emailVerificationDao.createEmailVerification(
            UserEmailVerificationEntity(
            userId = user.userId!!,
            salt = emailSalt,
            code = emailCodeHash
        ))

        // Send confirmation email
        emailService.sendEmailValidation(
            SendEmailVerificationRequest(
            to = adminEmail,
            subject = "verify email",
            code = emailCode
        ))
    }

    @Transactional(rollbackFor = [ Throwable::class ])
    fun createUser (
        email: String,
        displayName: String,
        accountId: Long,
        password: String
    ) {
        // Setup User
        val salt = createRandomString(16)
        val hash = hashPassword(password, salt)

        val user = UserEntity (
            email = email,
            emailVerified = false,
            displayName = displayName,
            superAdmin = false,
            accountId = accountId,
            accountAdmin = false,
            salt = salt,
            password = hash
        )
        userDao.createUser(user)

        val emailSalt = createRandomString(16)
        val emailCode = createRandomString(32)
        val emailCodeHash = hashPassword(emailCode, emailSalt)

        emailVerificationDao.createEmailVerification(UserEmailVerificationEntity(
            userId = user.userId!!,
            salt = emailSalt,
            code = emailCodeHash
        ))

        // Send confirmation email
        emailService.sendEmailValidation(SendEmailVerificationRequest(
            to = email,
            subject = "verify email",
            code = emailCode
        ))
    }

    @Transactional(rollbackFor = [ Throwable::class ])
    fun getUsers(accountId: Long): List<User> =
        userDao.readUserByAccount(accountId).map { it.toUser() }

    @Transactional(rollbackFor = [ Throwable::class ])
    fun deleteUser(
        accountId: Long,
        currentUserId: Long,
        deleteUserId: Long
    ) {
        if(currentUserId==deleteUserId) {
            // An account admin cannot delete their own account
            throw BadRequest()
        }

        val user = userDao.readUser(deleteUserId) ?: throw NotFoundException()
        if(user.accountId!=accountId) {
            // An account admin cannot delete users from another account
            throw ForbiddenException()
        }

        if(userDao.deleteUser(deleteUserId)<1) {
            // Bad timing, probably
            throw NotFoundException()
        }
    }
}