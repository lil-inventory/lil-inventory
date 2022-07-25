package org.ivcode.inventory.repository

import org.apache.ibatis.annotations.*
import org.ivcode.inventory.repository.model.GroupEntity
import org.ivcode.inventory.repository.model.GroupPathEntity

/**
 * Create a group
 * The inventory id must equal the parent's inventory id
 */
private const val CREATE_GROUP = """<script>
    INSERT INTO `group` (inventory_id, parent_group_id, name)
    <choose>
        <when test="parentGroupId != null">
            SELECT #{inventoryId}, #{parentGroupId}, #{name}
            FROM `group`
            WHERE `group`.group_id=#{parentGroupId} AND `group`.inventory_id=#{inventoryId}
        </when>
        <otherwise>
            VALUES (#{inventoryId}, #{parentGroupId}, #{name})
        </otherwise>
    </choose>
</script>"""

private const val READ_GROUP =
    "SELECT * FROM `group` WHERE inventory_id=#{inventoryId} AND group_id=#{groupId}"

private const val READ_GROUPS_BY_PARENT = """<script>
    SELECT * FROM `group` WHERE inventory_id=#{inventoryId} AND
    <choose>
        <when test="parentGroupId != null">parent_group_id = #{parentGroupId}</when>
        <otherwise>parent_group_id IS NULL</otherwise>
    </choose>
</script>"""

private const val READ_GROUP_AND_PATH = """
    WITH RECURSIVE rgroup (group_id, inventory_id, parent_group_id, name, `path`, recursive_group_id) AS (

	    SELECT group_id, inventory_id, parent_group_id, name, JSON_ARRAY(), parent_group_id
        FROM `group`
        WHERE group_id = #{groupId}

	    UNION ALL

	    SELECT r.group_id, r.inventory_id, r.parent_group_id, r.name, JSON_ARRAY_INSERT(r.`path`, '${'$'}[0]', JSON_OBJECT('groupId', g.group_id, 'name', g.name)), g.parent_group_id
        FROM `group` g INNER JOIN rgroup r ON g.group_id = r.recursive_group_id
        WHERE r.group_id = #{groupId}
    )
    SELECT group_id, inventory_id, parent_group_id, name, `path`
    FROM rgroup
    WHERE recursive_group_id IS NULL
"""

/**
 * Update group.
 * The inventory id must be equal to the parent's inventory id
 */
private const val UPDATE_GROUP = """<script>
    UPDATE
        `group`
    <if test="parentGroupId != null">
        INNER JOIN `group` AS g2 ON `group`.parent_group_id=g2.group_id
    </if>
    SET
        `group`.inventory_id=#{inventoryId},
        `group`.parent_group_id=#{parentGroupId},
        `group`.name=#{name}
    <choose>
        <when test="parentGroupId != null">
            WHERE `group`.group_id=#{groupId} AND g2.group_id=#{parentGroupId} AND g2.inventory_id=#{inventoryId}
        </when>
        <otherwise>
            WHERE inventory_id=#{inventoryId} AND group_id=#{groupId}
        </otherwise>
    </choose>
</script>"""

private const val DELETE_GROUP =
    "DELETE FROM `group` WHERE inventory_id=#{inventoryId} AND group_id=#{groupId} "

@Mapper
interface GroupDao {

    @Insert(CREATE_GROUP)
    @Options(useGeneratedKeys = true, keyProperty = "groupId", keyColumn = "group_id")
    fun createGroup(groupEntity: GroupEntity): Int

    @Select(READ_GROUP)
    @Result(property = "groupId", column = "group_id")
    @Result(property = "inventoryId", column = "inventory_id")
    @Result(property = "parentGroupId", column = "parent_group_id")
    @Result(property = "name", column = "name")
    fun readGroup(inventoryId: Long, groupId: Long): GroupEntity?

    @Select(READ_GROUPS_BY_PARENT)
    @Result(property = "groupId", column = "group_id")
    @Result(property = "inventoryId", column = "inventory_id")
    @Result(property = "parentGroupId", column = "parent_group_id")
    @Result(property = "name", column = "name")
    fun readGroupsByParent(inventoryId: Long, parentGroupId: Long?): List<GroupEntity>

    @Select(READ_GROUP_AND_PATH)
    @Result(property = "groupId", column = "group_id")
    @Result(property = "inventoryId", column = "inventory_id")
    @Result(property = "parentGroupId", column = "parent_group_id")
    @Result(property = "name", column = "name")
    @Result(property = "path", column = "path")
    fun readGroupPath(inventoryId: Long, groupId: Long): GroupPathEntity?

    @Update(UPDATE_GROUP)
    fun updateGroup(inventoryId: Long, groupId: Long, parentGroupId: Long?, name: String): Int

    @Delete(DELETE_GROUP)
    fun deleteGroup(inventoryId: Long, groupId: Long): Int
}
