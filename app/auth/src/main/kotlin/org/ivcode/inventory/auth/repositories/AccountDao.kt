package org.ivcode.inventory.auth.repositories

import org.apache.ibatis.annotations.*
import org.ivcode.inventory.auth.repositories.model.AccountEntity

private const val CREATE_ACCOUNT = """
    INSERT INTO `account` (`name`, `user_count`)
    VALUES (#{name}, #{userCount})
"""

private const val READ_ACCOUNT = """
    SELECT * FROM `account` WHERE account_id=#{accountId}
"""

private const val UPDATE_ACCOUNT = """
    UPDATE `account`
    SET `name`=#{name}, user_count=#{userCount}
    WHERE account_id=#{accountId} 
"""

private const val DELETE_ACCOUNT = """
    DELETE FROM `account`
    WHERE account_id=#{accountId}
"""

@Mapper
interface AccountDao {

    @Insert(CREATE_ACCOUNT)
    @Options(useGeneratedKeys=true, keyProperty="accountId")
    fun createAccount(accountEntity: AccountEntity): Int

    @Select(READ_ACCOUNT)
    @Result(property = "accountId", column = "account_id")
    @Result(property = "name", column = "name")
    @Result(property = "userCount", column = "user_count")
    fun readAccount(accountId: Long): AccountEntity?

    @Update(UPDATE_ACCOUNT)
    fun updateAccount(accountId: Long, name: String, userCount: Int): Int

    @Delete
    fun deleteAccount(accountId: Long): Int
}