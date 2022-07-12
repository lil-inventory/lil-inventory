package org.ivcode.inventory.repository

import org.apache.ibatis.annotations.Mapper
import org.apache.ibatis.annotations.Result
import org.apache.ibatis.annotations.Select
import org.ivcode.inventory.repository.model.AssetEntity
import org.ivcode.inventory.repository.model.PageInfo

private const val SEARCH_ASSET = """<script>
    SELECT
        *
    FROM
        asset
    <where>
        name LIKE '%' || #{keyword} || '%'
        OR barcode LIKE '%' || #{keyword} || '%'
    </where>
    <if test="pageInfo!=null">
    LIMIT
        #{pageInfo.offset}, #{pageInfo.size}
    </if>
</script>"""

private const val FOUND_ROWS = "SELECT FOUND_ROWS()"

@Mapper
interface SearchDao {

    @Select(SEARCH_ASSET)
    @Result(property = "assetId", column = "asset_id")
    @Result(property = "name", column = "name")
    @Result(property = "type", column = "asset_type")
    @Result(property = "barcode", column = "barcode")
    @Result(property = "quantity", column = "quantity")
    @Result(property = "quantityMinimum", column = "quantity_minimum")
    @Result(property = "quantityTotal", column = "quantity_total")
    @Result(property = "groupId", column="group_id")
    fun searchAssets(keyword: String, pageInfo: PageInfo? = null): List<AssetEntity>

    @Select(FOUND_ROWS)
    fun foundRows(): Int
}