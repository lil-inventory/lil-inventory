package org.ivcode.inventory.auth.repositories

import org.apache.ibatis.annotations.*
import org.ivcode.inventory.auth.repositories.model.UserEmailVerificationEntity

private const val CREATE_EMAIL_VERIFICATION = """
    INSERT INTO user_email_verification (user_id, salt, code)
    VALUES (#{userId}, #{salt}, #{code})
"""

private const val READ_EMAIL_VERIFICATION = """
    SELECT
        v.*
    FROM
        user_email_verification AS v INNER JOIN user AS u ON u.user_id = v.user_id
    WHERE
        u.user_email=LOWER(#{email})
"""

private const val DELETE_EMAIL_VERIFICATION = """
    DELETE FROM user_email_verification
    WHERE user_id=#{userId}
        
"""

@Mapper
interface UserEmailVerificationDao {

    @Insert(CREATE_EMAIL_VERIFICATION)
    @Options(useGeneratedKeys=true, keyProperty="userEmailVerificationId")
    fun createEmailVerification(emailVerificationEntity: UserEmailVerificationEntity): Int

    @Select(READ_EMAIL_VERIFICATION)
    @Result(property = "userEmailVerificationId", column = "user_email_verification_id")
    @Result(property = "userId", column = "user_id")
    @Result(property = "salt", column = "salt")
    @Result(property = "code", column = "code")
    fun readEmailVerification(email: String): UserEmailVerificationEntity?

    @Delete(DELETE_EMAIL_VERIFICATION)
    fun deleteEmailVerification(userId: Long): Int
}