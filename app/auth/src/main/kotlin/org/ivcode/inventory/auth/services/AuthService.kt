package org.ivcode.inventory.auth.services

import io.jsonwebtoken.JwtException
import org.ivcode.inventory.auth.repositories.UserDao
import org.ivcode.inventory.auth.repositories.UserEmailVerificationDao
import org.ivcode.inventory.auth.repositories.UserSessionDao
import org.ivcode.inventory.auth.repositories.model.UserEmailVerificationEntity
import org.ivcode.inventory.auth.repositories.model.UserEntity
import org.ivcode.inventory.auth.repositories.model.UserSessionEntity
import org.ivcode.inventory.auth.services.model.AccessToken
import org.ivcode.inventory.auth.utils.*
import org.ivcode.inventory.auth.utils.hashPassword
import org.ivcode.inventory.auth.utils.toAccessToken
import org.ivcode.inventory.auth.utils.toIdentity
import org.ivcode.inventory.common.exception.BadRequest
import org.ivcode.inventory.common.exception.UnauthorizedException
import org.ivcode.inventory.email.service.EmailService
import org.ivcode.inventory.email.service.model.SendEmailVerificationRequest
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.*

@Service
class AuthService(
    private val userDao: UserDao,
    private val userSessionDao: UserSessionDao,
    private val emailVerificationDao: UserEmailVerificationDao,
    private val authJwtService: AuthJwtService,
    private val emailService: EmailService
) {

    @Transactional(rollbackFor = [ Throwable::class ])
    fun createUser (
        email: String,
        displayName: String,
        password: String
    ) {
        // Setup User
        val salt = createRandomString(16)
        val hash = hashPassword(password, salt)

        val user = UserEntity (
            email = email,
            emailVerified = false,
            displayName = displayName,
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
    fun verifyEmail(
        email: String,
        code: String
    ) {
        val emailVerification = emailVerificationDao.readEmailVerification(email) ?: throw UnauthorizedException()
        if(verifyPassword(code, emailVerification.salt, emailVerification.code)) {
            userDao.updateEmailVerified(emailVerification.userId, true)
            emailVerificationDao.deleteEmailVerification(emailVerification.userId)
        } else {
            throw UnauthorizedException()
        }
    }

    @Transactional(rollbackFor = [ Throwable::class ])
    fun sendResetPasswordEmail(email: String) {
        val user = userDao.readUserByEmail(email)
        if(user?.emailVerified != true) {
            throw BadRequest("email not verified")
        }

        TODO()
    }

    @Transactional(rollbackFor = [ Throwable::class ])
    fun grantAccess(
        grantType: String,
        username: String?,
        password: String?,
        refreshToken: String?
    ): AccessToken {
        return when(grantType) {
            "password" -> login(username, password)
            "refresh_token" -> refresh(refreshToken)
            else -> throw BadRequest()
        }
    }

    private fun refresh(refreshToken: String?): AccessToken {
        if(refreshToken==null) {
            throw BadRequest()
        }

        val claims = try {
            authJwtService.getClaims(refreshToken)
        } catch (e: JwtException) {
            // TODO revoke session
            throw UnauthorizedException(e.message, e)
        }

        val id = claims.id
        val sessionId = claims.sessionId()

        val userSessionEntity = userSessionDao.readUserSession(sessionId) ?: throw UnauthorizedException()
        if(id != userSessionEntity.jwtId) {
            // TODO revoke session
            throw UnauthorizedException()
        }

        val userEntity = userDao.readUser(userSessionEntity.userId) ?: throw UnauthorizedException()
        val identity = userEntity.toIdentity()

        val tokenInfo = authJwtService.createTokens(
            identity = identity,
            sessionId = UUID.fromString(userSessionEntity.userSessionId))

        userSessionDao.updateUserSession (
            userSessionId = userSessionEntity.userSessionId,
            jwtId = tokenInfo.refresh.jwtId,
            expiration = tokenInfo.refresh.expiration
        )

        return tokenInfo.toAccessToken()
    }

    private fun login(email: String?, password: String?): AccessToken {
        if(email==null || password==null) {
            throw BadRequest()
        }

        val userEntity = userDao.readUserByEmail(email) ?: throw UnauthorizedException()

        if(!verifyPassword(password, userEntity.salt, userEntity.password)) {
            throw UnauthorizedException()
        }

        val identity = userEntity.toIdentity()
        val tokenInfo = authJwtService.createTokens(identity)

        val entity = UserSessionEntity(
            userSessionId = tokenInfo.refresh.sessionId.toString(),
            userId = userEntity.userId!!,
            jwtId = tokenInfo.refresh.jwtId,
            expiration = tokenInfo.refresh.expiration
        )

        userSessionDao.deleteUserSessionsByUser(userEntity.userId)
        userSessionDao.createUserSession(entity)

        return tokenInfo.toAccessToken()
    }
}
