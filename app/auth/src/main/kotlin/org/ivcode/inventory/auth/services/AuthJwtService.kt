package org.ivcode.inventory.auth.services

import io.jsonwebtoken.*
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.jvm.Throws

@Service
class AuthJwtService (
    @Value("\${application.key}") private val appKey: String,
    private val accessTTL: Long = TimeUnit.MINUTES.toMillis(15),
    private val refreshTTL: Long = TimeUnit.HOURS.toMillis(3)
) {

    companion object {
        const val TOKEN_TYPE_ACCESS = "access"
        const val TOKEN_TYPE_REFRESH = "refresh"
    }

    /**
     * Create access, refresh and refresh tokens
     * @param user user information
     * @param sessionId the session id if it already exists. A new session id will be returned if not defined
     */
    fun createTokens(identity: Identity, sessionId: UUID = UUID.randomUUID()): TokenInfo {
        val now = Date()
        return TokenInfo(
            access = createAccessToken(identity, now),
            refresh = createRefreshToken(sessionId, now)
        )
    }

    private fun createAccessToken(identity: Identity, now: Date = Date()): AccessTokenInfo {
        val expiration = Date(now.time + accessTTL)

        return AccessTokenInfo(
            expiration = expiration,
            token = createToken(
                claims = createAccessClaims(identity),
                issuedAt = now,
                expiration = expiration,
                signingKey = appKey
            )
        )
    }

    private fun createRefreshToken(sessionId: UUID, now: Date = Date()): RefreshTokenInfo {
        val jwtId = UUID.randomUUID().toString()
        val expiration = Date(now.time + refreshTTL)

        return RefreshTokenInfo(
            sessionId = sessionId,
            jwtId = jwtId,
            expiration = expiration,
            token = createToken(
                id = jwtId,
                issuedAt = now,
                expiration = expiration,
                claims = createRefreshClaims(sessionId.toString()),
                signingKey = appKey,
            )
        )
    }

    private fun createAccessClaims(identity: Identity): Map<String, *> =
        mutableMapOf (
            "email" to identity.email,
            "identity" to createIdentityClaims(identity),
            "typ" to TOKEN_TYPE_ACCESS
        )

    private fun createIdentityClaims(identity: Identity): Map<String, *> =
        mutableMapOf (
            "user_id" to identity.userId,
            "email" to identity.email,
            "email-verified" to identity.emailVerified,
            "display-name" to identity.displayName
        )

    private fun createRefreshClaims(sessionId: String): Map<String, *> =
        mutableMapOf(
            "sid" to sessionId,
            "typ" to TOKEN_TYPE_REFRESH
        )

    /**
     * Get claims and validate token
     * @throws JwtException if jwt can't be verified
     */
    fun getClaims(token: String): Claims = getClaims(token, appKey)

    fun email(claims: Claims): String? = claims["email"] as String

    fun identity(claims: Claims): Identity? {
        val identity = claims["identity"] as Map<String, *>? ?: return null
        return Identity(
            userId = (identity["user_id"] as Number).toLong(),
            email = identity["email"] as String,
            emailVerified = identity["email-verified"] as Boolean,
            displayName = identity["display-name"] as String
        )
    }

    /**
     * Validate a token against the given secret
     */
    fun validateToken(token: String, signingKey: String = appKey) {
        getClaims(token, signingKey)
    }

    /**
     * Get claims and validate token
     * @throws JwtException if jwt can't be verified
     */
    fun getClaims(token: String, signingKey: String): Claims {
        val claims = Jwts.parser()
            .setSigningKey(signingKey)
            .parseClaimsJws(token)
            .body

        validateClaims(claims)

        return claims
    }

    /**
     * Validates the given claims. If validation fails, a [JwtException] is thrown
     * @throws JwtException if validation fails
     */
    @Throws(JwtException::class)
    fun validateClaims(claims: Claims) {
        // check expiration
        val expiration = claims.expiration ?: throw JwtException("missing claim \"exp\" claim")
        if(expiration.before(Date())) {
            throw JwtException("expired jwt")
        }
    }

    protected fun createToken(
        claims: Map<String, *>,
        id: String? = null,
        issuer: String? = null,
        issuedAt: Date,
        expiration: Date,
        signingKey: String
    ): String = Jwts.builder()
        .setClaims(claims)
        .setId(id)
        .setIssuer(issuer)
        .setIssuedAt(issuedAt)
        .setExpiration(expiration)
        .signWith(SignatureAlgorithm.HS512, signingKey)
        .compact()
}

/**
 * Contains information about the access, identity, and refresh tokens
 */
data class TokenInfo (
    val access: AccessTokenInfo,
    val refresh: RefreshTokenInfo
)

/**
 * Contains access token information
 */
data class AccessTokenInfo (
    val expiration: Date,
    val token: String
)

/**
 * Contains refresh token information
 */
data class RefreshTokenInfo (
    val sessionId: UUID,
    val jwtId: String,
    val expiration: Date,
    val token: String,
)

data class Identity (
    val userId: Long,
    val email: String,
    val emailVerified: Boolean,
    val displayName: String
)

fun Claims.sessionId(): String = this["sid"] as String
