package org.ivcode.inventory.repository

import org.apache.ibatis.annotations.*
import org.ivcode.inventory.repository.model.AssetEntity

/**
 * Create a new record.
 * Only create if the group's inventory id is the same
 */
private const val CREATE_ASSET = """<script>
    INSERT INTO asset (inventory_id, name, barcode, group_id)
    <choose>
        <when test="groupId != null">
            SELECT #{inventoryId}, #{name}, #{barcode}, #{groupId}
            FROM `group` 
            WHERE `group`.group_id=#{groupId} AND `group`.inventory_id=#{inventoryId}
        </when>
        <otherwise>
            VALUES (#{inventoryId}, #{name}, #{barcode}, #{groupId})        
        </otherwise>
    </choose>
</script>"""

private const val READ_ASSET =
    "SELECT * FROM asset WHERE inventory_id=#{inventoryId} AND asset_id=#{assetId}"

private const val READ_ASSETS_BY_GROUP = """<script>
    SELECT
        *
    FROM
        asset
    WHERE
        inventory_id=#{inventoryId} AND 
    <choose>
        <when test="groupId != null">group_id=#{groupId}</when>
        <otherwise>group_id IS NULL</otherwise>
    </choose>
</script>"""

/**
 * Update asset.
 * The inventory id must equal the group's inventory id
 */
private const val UPDATE_ASSET = """<script>
    UPDATE asset
    <if test="groupId != null">
        INNER JOIN `group` ON asset.group_id=`group`.group_id
    </if>
    SET
        asset.inventory_id=#{inventoryId},
        asset.name=#{name},
        asset.barcode=#{barcode},
        asset.group_id=#{groupId}
    <choose>
        <when test="groupId != null">
            WHERE inventory_id=#{inventoryId} AND asset_id=#{assetId} AND `group`.group_id=#{groupId} AND `group`.inventory_id=#{inventoryId}
        </when>
        <otherwise>
            WHERE inventory_id=#{inventoryId} AND asset_id=#{assetId}
        </otherwise>
    </choose>
</script>"""

private const val DELETE_ASSET =
    "DELETE FROM asset WHERE inventory_id=#{inventoryId} AND asset_id=#{assetId}"



@Mapper
interface AssetDao {

    @Insert(CREATE_ASSET)
    @Options(useGeneratedKeys = true, keyProperty = "assetId", keyColumn = "asset_id")
    fun createAsset(assetEntity: AssetEntity): Int

    @Select(READ_ASSET)
    @Result(property = "assetId", column = "asset_id")
    @Result(property = "inventoryId", column = "inventory_id")
    @Result(property = "name", column = "name")
    @Result(property = "barcode", column = "barcode")
    @Result(property = "groupId", column="group_id")
    fun readAsset(inventoryId: Long, assetId: Long): AssetEntity?

    @Select(READ_ASSETS_BY_GROUP)
    @Result(property = "assetId", column = "asset_id")
    @Result(property = "inventoryId", column = "inventory_id")
    @Result(property = "name", column = "name")
    @Result(property = "barcode", column = "barcode")
    @Result(property = "groupId", column="group_id")
    fun readAssetsByGroup(inventoryId: Long, groupId: Long?): List<AssetEntity>

    @Update(UPDATE_ASSET)
    fun updateAsset(assetEntity: AssetEntity): Int

    @Delete(DELETE_ASSET)
    fun deleteAsset(inventoryId: Long, assetId: Long): Int
}
