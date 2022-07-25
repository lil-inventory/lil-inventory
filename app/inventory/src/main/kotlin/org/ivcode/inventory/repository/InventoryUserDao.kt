package org.ivcode.inventory.repository

import org.apache.ibatis.annotations.*
import org.ivcode.inventory.repository.model.InventoryUserEntity

private const val CREATE_INVENTORY_USER = """
    INSERT INTO `inventory_user` (inventory_id, user_id, `read`, `write`, `admin`)
    VALUES (#{inventoryId}, #{userId}, #{read}, #{write}, #{admin})
"""

private const val READ_INVENTORY_USER = """
    SELECT * FROM `inventory_user` where inventory_id=#{inventoryId} AND user_id=#{userId}
"""

private const val UPDATE_INVENTORY_USER = """
    UPDATE `inventory_user`
    SET `read`=#{read}, `write`=#{write}, `admin`=#{admin}
    WHERE inventory_id=#{inventoryId} AND user_id=#{userId}
"""

private const val DELETE_INVENTORY_USER = """
    DELETE FROM `inventory_user`
    WHERE inventory_id=#{inventoryId} AND user_id=#{userId}
"""

@Mapper
interface InventoryUserDao {

    @Insert(CREATE_INVENTORY_USER)
    fun createInventoryUser(inventoryUserEntity: InventoryUserEntity): Int

    @Select(READ_INVENTORY_USER)
    @Result(property = "inventoryId", column = "inventory_id")
    @Result(property = "userId", column = "user_id")
    @Result(property = "read", column = "read")
    @Result(property = "write", column = "write")
    @Result(property = "admin", column = "admin")
    fun readInventoryUser(inventoryId: Long, userId: Long): InventoryUserEntity?

    @Update(UPDATE_INVENTORY_USER)
    fun updateInventoryUser (
        inventoryId: Long,
        userId: Long,
        read: Boolean,
        write: Boolean,
        admin: Boolean
    ): Int

    @Delete(DELETE_INVENTORY_USER)
    fun deleteInventoryUser (inventoryId: Long, userId: Long): Int
}
