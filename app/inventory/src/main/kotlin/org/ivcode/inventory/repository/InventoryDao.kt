package org.ivcode.inventory.repository

import org.apache.ibatis.annotations.*
import org.ivcode.inventory.repository.model.InventoryEntity

private const val CREATE_INVENTORY = """
    INSERT INTO `inventory` (name)
    VALUES (#{name})
"""

private const val READ_INVENTORIES = """
    SELECT * FROM `inventory`
"""

private const val READ_INVENTORY = """
    SELECT * FROM `inventory` WHERE inventory_id=#{inventoryId}
"""

private const val UPDATE_INVENTORY = """
    UPDATE `inventory`
    SET name=#{name}
    WHERE inventory_id=#{inventoryId}
"""

@Mapper
interface InventoryDao {

    @Insert(CREATE_INVENTORY)
    @Options(useGeneratedKeys = true, keyProperty = "inventoryId", keyColumn = "inventory_id")
    fun createInventory(inventoryEntity: InventoryEntity): Int

    @Select(READ_INVENTORIES)
    @Result(property = "inventoryId", column = "inventory_id")
    @Result(property = "name", column = "name")
    fun readInventories(): List<InventoryEntity>

    @Select(READ_INVENTORY)
    @Result(property = "inventoryId", column = "inventory_id")
    @Result(property = "name", column = "name")
    fun readInventory(inventoryId: Long): InventoryEntity?

    @Update(UPDATE_INVENTORY)
    fun updateInventory(inventoryId: Long, name: String): Int
}