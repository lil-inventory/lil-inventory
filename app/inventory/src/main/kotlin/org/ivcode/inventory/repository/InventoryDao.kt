package org.ivcode.inventory.repository

import org.apache.ibatis.annotations.*
import org.ivcode.inventory.repository.model.InventoryEntity

private const val CREATE_INVENTORY = """
    INSERT INTO `inventory` (name, private, account_id, user_id)
    VALUES (#{name}, #{private}, #{accountId}, #{userId})
"""

private const val READ_INVENTORIES = """
    SELECT
        i.*
    FROM
        `inventory` AS i LEFT JOIN `inventory_user` AS iu ON i.inventory_id=iu.inventory_id
    WHERE
        i.owner_user_id=#{userId} OR iu.user_id=#{userId} 
"""

private const val READ_ACCOUNT_INVENTORIES = """
    SELECT
        i.*
    FROM
        `inventory` AS i LEFT JOIN `inventory_user` AS iu ON i.inventory_id=iu.inventory_id
    WHERE
        (i.account_id=#{accountId} AND i.user_id IS NULL)
        AND (
            i.private=false
            OR (iu.user_id=#{userId} AND (iu.read=true OR iu.write=true OR iu.admin=true))
        )
"""

private const val READ_INVENTORY = """
    SELECT * FROM `inventory` WHERE inventory_id=#{inventoryId}
"""

private const val UPDATE_INVENTORY = """
    UPDATE `inventory`
    SET name=#{name}
    WHERE inventory_id=#{inventoryId}
"""

private const val DELETE_INVENTORY = """
    DELETE FROM `inventory` WHERE inventory_id=#{inventoryId}
"""

@Mapper
interface InventoryDao {

    @Insert(CREATE_INVENTORY)
    @Options(useGeneratedKeys = true, keyProperty = "inventoryId", keyColumn = "inventory_id")
    fun createInventory(inventoryEntity: InventoryEntity): Int

    @Select(READ_INVENTORIES)
    @Result(property = "inventoryId", column = "inventory_id")
    @Result(property = "name", column = "name")
    @Result(property = "ownerUserId", column = "owner_user_id")
    fun readInventories(userId: Long): List<InventoryEntity>

    @Select(READ_ACCOUNT_INVENTORIES)
    @Result(property = "inventoryId", column = "inventory_id")
    @Result(property = "name", column = "name")
    @Result(property = "ownerUserId", column = "owner_user_id")
    fun readAccountInventories(accountId: Long, userId: Long): List<InventoryEntity>

    @Select(READ_INVENTORY)
    @Result(property = "inventoryId", column = "inventory_id")
    @Result(property = "name", column = "name")
    @Result(property = "ownerUserId", column = "owner_user_id")
    fun readInventory(inventoryId: Long): InventoryEntity?

    @Update(UPDATE_INVENTORY)
    fun updateInventory(inventoryId: Long, name: String): Int

    @Delete(DELETE_INVENTORY)
    fun deleteInventory(inventoryId: Long): Int
}