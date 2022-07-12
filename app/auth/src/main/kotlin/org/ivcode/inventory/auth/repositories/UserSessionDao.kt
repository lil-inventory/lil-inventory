package org.ivcode.inventory.auth.repositories

import org.apache.ibatis.annotations.*
import org.ivcode.inventory.auth.repositories.model.UserSessionEntity
import java.util.*

private const val CREATE_SESSION = """
    INSERT INTO `user_session` (`user_session_id`, `user_id`, `jwt_id`, `expiration`)
    VALUES (#{userSessionId}, #{userId}, #{jwtId}, #{expiration})
"""

private const val READ_SESSION = """
    SELECT * FROM user_session WHERE user_session_id=#{userSessionId}
"""

private const val UPDATE_SESSION = """
    UPDATE user_session
    SET `jwt_id`=#{jwtId}, expiration=#{expiration}
    WHERE user_session_id=#{userSessionId}
"""

private const val DELETE_SESSION = """
    DELETE FROM user_session
    WHERE user_session_id=#{userSessionId}
"""

private const val DELETE_SESSIONS_BY_USER = """
    DELETE FROM user_session
    WHERE user_id=#{userId}
"""

@Mapper
interface UserSessionDao {

    @Insert(CREATE_SESSION)
    fun createUserSession(userSession: UserSessionEntity);

    @Select(READ_SESSION)
    @Result(property = "userSessionId", column = "user_session_id")
    @Result(property = "userId", column = "user_id")
    @Result(property = "jwtId", column = "jwt_id")
    @Result(property = "expiration", column = "expiration")
    fun readUserSession(userSessionId: String): UserSessionEntity?

    @Update(UPDATE_SESSION)
    fun updateUserSession(userSessionId: String, jwtId: String, expiration: Date): Int

    @Delete(DELETE_SESSION)
    fun deleteUserSession(userSessionId: String): Int

    @Delete(DELETE_SESSIONS_BY_USER)
    fun deleteUserSessionsByUser(userId: Long): Int
}