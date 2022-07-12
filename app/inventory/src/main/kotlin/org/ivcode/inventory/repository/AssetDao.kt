package org.ivcode.inventory.repository

import org.apache.ibatis.annotations.*
import org.ivcode.inventory.repository.model.AssetEntity

private const val CREATE_ASSET = """
    INSERT INTO asset (name, asset_type, barcode, quantity, quantity_minimum, group_id)
    VALUES (#{name}, #{type}, #{barcode}, #{quantity}, #{quantityMinimum}, #{groupId})
"""

private const val READ_ASSET =
    "SELECT * FROM asset WHERE asset_id=#{assetId}"

private const val READ_ASSETS_BY_GROUP = """<script>
    SELECT * FROM asset WHERE 
    <choose>
        <when test="groupId != null">group_id=#{groupId}</when>
        <otherwise>group_id IS NULL</otherwise>
    </choose>
</script>"""

private const val UPDATE_ASSET = """
    UPDATE asset
    SET name=#{name}, asset_type=#{type}, barcode=#{barcode}, quantity=#{quantity}, quantity_minimum=#{quantityMinimum}, group_id=#{groupId}
    WHERE asset_id=#{assetId}
"""

private const val DELETE_ASSET =
    "DELETE FROM asset WHERE asset_id=#{assetId}"

private const val ADD_QUANTITY = """
    UPDATE asset
    SET quantity = quantity + #{quantity}
    WHERE asset_id=#{assetId}
"""


@Mapper
interface AssetDao {

    @Insert(CREATE_ASSET)
    @Options(useGeneratedKeys = true, keyProperty = "assetId", keyColumn = "asset_id")
    fun createAsset(assetEntity: AssetEntity): Int

    @Select(READ_ASSET)
    @Result(property = "assetId", column = "asset_id")
    @Result(property = "name", column = "name")
    @Result(property = "type", column = "asset_type")
    @Result(property = "barcode", column = "barcode")
    @Result(property = "quantity", column = "quantity")
    @Result(property = "quantityMinimum", column = "quantity_minimum")
    @Result(property = "quantityTotal", column = "quantity_total")
    @Result(property = "groupId", column="group_id")
    fun readAsset(assetId: Int): AssetEntity?

    @Select(READ_ASSETS_BY_GROUP)
    @Result(property = "assetId", column = "asset_id")
    @Result(property = "name", column = "name")
    @Result(property = "type", column = "asset_type")
    @Result(property = "barcode", column = "barcode")
    @Result(property = "quantity", column = "quantity")
    @Result(property = "quantityMinimum", column = "quantity_minimum")
    @Result(property = "quantityTotal", column = "quantity_total")
    @Result(property = "groupId", column="group_id")
    fun readAssetsByGroup(groupId: Int?): List<AssetEntity>

    @Update(UPDATE_ASSET)
    fun updateAsset(assetEntity: AssetEntity): Int

    @Delete(DELETE_ASSET)
    fun deleteAsset(assetId: Int): Int

    @Update(ADD_QUANTITY)
    fun addQuantity(assetId: Int, quantity: Int): Int
}
