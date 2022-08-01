package org.ivcode.inventory.auth.services

import io.jsonwebtoken.*
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.jvm.Throws

private const val TOKEN_TYPE_ACCESS = "access"
private const val TOKEN_TYPE_REFRESH = "refresh"

private const val CLAIM_IDENTITY = "identity"
private const val CLAIM_IDENTITY_USER_ID = "user-id"
private const val CLAIM_IDENTITY_EMAIL = "email"
private const val CLAIM_IDENTITY_EMAIL_VERIFIED = "email-verified"
private const val CLAIM_IDENTITY_DISPLAY_NAME = "display-name"

private const val CLAIM_ACCOUNT = "account"
private const val CLAIM_ACCOUNT_ACCOUNT_ID = "account-id"
private const val CLAIM_ACCOUNT_NAME = "name"

private const val CLAIM_AUTHORITIES = "auths"

@Service
class AuthJwtService (
    @Value("\${application.key}") private val appKey: String,
    private val accessTTL: Long = TimeUnit.MINUTES.toMillis(15),
    private val refreshTTL: Long = TimeUnit.HOURS.toMillis(3)
) {

    /**
     * Create access, refresh and refresh tokens
     * @param user user information
     * @param sessionId the session id if it already exists. A new session id will be returned if not defined
     */
    fun createTokens(
        identity: Identity,
        account: Account? = null,
        authorities: List<GrantedAuthorities>? = null,
        sessionId: UUID = UUID.randomUUID()
    ): TokenInfo {
        val now = Date()
        return TokenInfo(
            access = createAccessToken(
                identity = identity,
                account = account,
                authorities = authorities,
                now = now),
            refresh = createRefreshToken(sessionId, now)
        )
    }

    private fun createAccessToken(
        identity: Identity,
        account: Account? = null,
        authorities: List<GrantedAuthorities>? = null,
        now: Date = Date()
    ): AccessTokenInfo {
        val expiration = Date(now.time + accessTTL)

        return AccessTokenInfo(
            expiration = expiration,
            token = createToken(
                claims = createAccessClaims(
                    identity = identity,
                    account = account,
                    authorities = authorities
                ),
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

    private fun createAccessClaims(
        identity: Identity,
        account: Account?,
        authorities: List<GrantedAuthorities>?
    ): Map<String, *> {
        val identityClaims = createIdentityClaims(identity)
        val accountClaims = createAccountClaims(account)
        val authorityClaims = createAuthorityClaims(authorities)

        val claims = mutableMapOf(
            "email" to identity.email,
            "typ" to TOKEN_TYPE_ACCESS,
            CLAIM_IDENTITY to identityClaims
        )
        if(accountClaims!=null) {
            claims[CLAIM_ACCOUNT] = accountClaims
        }
        if(authorityClaims!=null) {
            claims[CLAIM_AUTHORITIES] = authorityClaims
        }

        return claims
    }

    private fun createIdentityClaims(identity: Identity): Map<String, *> =
        mutableMapOf (
            CLAIM_IDENTITY_USER_ID to identity.userId,
            CLAIM_IDENTITY_EMAIL to identity.email,
            CLAIM_IDENTITY_EMAIL_VERIFIED to identity.emailVerified,
            CLAIM_IDENTITY_DISPLAY_NAME to identity.displayName
        )

    private fun createAccountClaims(account: Account?): Map<String, *>? {
        if(account==null) return null
        return mutableMapOf(
            CLAIM_ACCOUNT_ACCOUNT_ID to account.accountId,
            CLAIM_ACCOUNT_NAME to account.name
        )
    }

    private fun createAuthorityClaims(authorities: List<GrantedAuthorities>?): List<String>? {
        if(authorities==null || authorities.isEmpty()) return null
        return authorities.map { it.name }
    }

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
        val identity = claims[CLAIM_IDENTITY] as Map<String, *>? ?: return null
        return Identity(
            userId = (identity[CLAIM_IDENTITY_USER_ID] as Number).toLong(),
            email = identity[CLAIM_IDENTITY_EMAIL] as String,
            emailVerified = identity[CLAIM_IDENTITY_EMAIL_VERIFIED] as Boolean,
            displayName = identity[CLAIM_IDENTITY_DISPLAY_NAME] as String,
        )
    }

    fun account(claims: Claims): Account? {
        val account = claims[CLAIM_ACCOUNT] as Map<String, *>? ?: return null
        return Account(
            accountId = (account[CLAIM_ACCOUNT_ACCOUNT_ID] as Number).toLong(),
            name = account[CLAIM_ACCOUNT_NAME] as String
        )
    }

    fun grantedAuthorities(claims: Claims): Collection<GrantedAuthorities>? {
        val account = claims[CLAIM_AUTHORITIES] as List<String>? ?: return null
        return account.map { GrantedAuthorities.valueOf(it) }
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

data class Account (
    val accountId: Long,
    val name: String
)

enum class GrantedAuthorities {
    /** system administrator */
    SUPER_ADMIN,
    /** account administrator */
    ACCOUNT_ADMIN
}

fun Claims.sessionId(): String = this["sid"] as String

