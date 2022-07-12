package org.ivcode.inventory.auth.utils

import java.security.SecureRandom
import java.util.*
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.PBEKeySpec

private const val ALGORITHM = "PBKDF2WithHmacSHA512"
private const val ITERATION_COUNT = 65536
private const val KEY_LENGTH = 512
private const val RANDOM_DEFAULT_SIZE = 16


/**
 * Creates a hash based on the given password and salt
 */
internal fun hashPassword(password: String, salt: String): String {
    val saltData = Base64.getDecoder().decode(salt)

    val kepSpec = PBEKeySpec(password.toCharArray(), saltData, ITERATION_COUNT, KEY_LENGTH)
    val factory = SecretKeyFactory.getInstance(ALGORITHM)

    val hash = factory.generateSecret(kepSpec).encoded
    return Base64.getEncoder().encodeToString(hash)
}

/**
 * Creates a new salt
 */
internal fun createRandomString(size: Int = RANDOM_DEFAULT_SIZE): String {
    val random = SecureRandom()
    val salt = ByteArray(size)
    random.nextBytes(salt)

    return Base64.getEncoder().encodeToString(salt)
}

/**
 * Verifies the password matches the given hash
 */
internal fun verifyPassword(password: String, salt: String, hash: String): Boolean {
    val passwordHash = hashPassword(password, salt)
    return passwordHash == hash
}