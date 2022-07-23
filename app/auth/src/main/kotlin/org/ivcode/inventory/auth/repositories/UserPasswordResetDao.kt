package org.ivcode.inventory.auth.repositories

import org.apache.ibatis.annotations.*
import org.ivcode.inventory.auth.repositories.model.UserPasswordResetEntity

private const val CREATE_PASSWORD_RESET = """
    INSERT INTO user_password_reset (user_id, salt, code, expiration)
    VALUES (#{userId}, #{salt}, #{code}, #{expiration})
"""

private const val READ_PASSWORD_RESET = """
    SELECT
        r.*
    FROM
        user_password_reset AS r INNER JOIN user AS u ON u.user_id = r.user_id
    WHERE
        u.user_email=LOWER(#{email})
"""

private const val DELETE_PASSWORD_RESET = """
    DELETE FROM user_password_reset WHERE user_id=#{userId}
"""

@Mapper
interface UserPasswordResetDao {

    @Insert(CREATE_PASSWORD_RESET)
    @Options(useGeneratedKeys=true, keyProperty="userPasswordResetId")
    fun createPasswordReset(userPasswordReset: UserPasswordResetEntity): Int

    @Select(READ_PASSWORD_RESET)
    @Result(property = "userPasswordResetId", column = "user_password_reset_id")
    @Result(property = "userId", column = "user_id")
    @Result(property = "salt", column = "salt")
    @Result(property = "code", column = "code")
    fun readPasswordReset(email: String): UserPasswordResetEntity?

    @Delete(DELETE_PASSWORD_RESET)
    fun deletePasswordReset(userId: Long): Int
}