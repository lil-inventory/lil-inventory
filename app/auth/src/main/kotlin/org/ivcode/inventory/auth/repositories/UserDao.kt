package org.ivcode.inventory.auth.repositories

import org.apache.ibatis.annotations.*
import org.ivcode.inventory.auth.repositories.model.UserEntity

private const val CREATE_USER = """
    INSERT INTO `user` (user_email, email_verified, display_name, salt, password)
    VALUES (LOWER(#{email}), #{emailVerified}, #{displayName}, #{salt}, #{password})
"""

private const val READ_USER_BY_EMAIL =
    "SELECT * FROM `user` WHERE user_email=LOWER(#{email})"

private const val READ_USER_BY_ID =
    "SELECT * FROM `user` WHERE user_id=#{userId}"

private const val UPDATE_EMAIL_VERIFIED = """
    UPDATE `user`
    SET email_verified=#{emailVerified}
    WHERE user_id=#{userId}
"""

private const val UPDATE_PASSWORD = """
    UPDATE `user`
    SET salt=#{salt}, password=#{password}
    WHERE user_id=#{userId}
"""

@Mapper
interface UserDao {

    @Insert(CREATE_USER)
    @Options(useGeneratedKeys=true, keyProperty="userId")
    fun createUser(userEntity: UserEntity): Int

    @Select(READ_USER_BY_EMAIL)
    @Result(property = "userId", column = "user_id")
    @Result(property = "email", column = "user_email")
    @Result(property = "emailVerified", column = "email_verified")
    @Result(property = "displayName", column = "display_name")
    @Result(property = "salt", column = "salt")
    @Result(property = "password", column = "password")
    fun readUserByEmail(email: String): UserEntity?

    @Select(READ_USER_BY_ID)
    @Result(property = "userId", column = "user_id")
    @Result(property = "email", column = "user_email")
    @Result(property = "emailVerified", column = "email_verified")
    @Result(property = "displayName", column = "display_name")
    @Result(property = "salt", column = "salt")
    @Result(property = "password", column = "password")
    fun readUser(userId: Long): UserEntity?

    @Update(UPDATE_EMAIL_VERIFIED)
    fun updateEmailVerified(userId: Long, emailVerified: Boolean): Int

    @Update(UPDATE_PASSWORD)
    fun updatePassword(userId: Long, salt: String, password: String): Int
}
