package org.ivcode.inventory.repository

import org.apache.ibatis.annotations.*
import org.ivcode.inventory.repository.model.GroupEntity
import org.ivcode.inventory.repository.model.GroupPathEntity

private const val CREATE_GROUP = """
    INSERT INTO `group` (parent_group_id, name)
    VALUES (#{parentGroupId}, #{name})
"""

private const val READ_GROUP =
    "SELECT * FROM `group` WHERE group_id=#{groupId}"

private const val READ_GROUPS_BY_PARENT = """<script>
    SELECT * FROM `group` WHERE 
    <choose>
        <when test="parentGroupId != null">parent_group_id = #{parentGroupId}</when>
        <otherwise>parent_group_id IS NULL</otherwise>
    </choose>
</script>"""

private const val READ_GROUP_AND_PATH = """
    WITH RECURSIVE rgroup (group_id, parent_group_id, name, `path`, recursive_group_id) AS (

	    SELECT group_id, parent_group_id, name, JSON_ARRAY(), parent_group_id
        FROM `group`
        WHERE group_id = #{groupId}

	    UNION ALL

	    SELECT r.group_id, r.parent_group_id, r.name, JSON_ARRAY_INSERT(r.`path`, '$[0]', JSON_OBJECT('groupId', g.group_id, 'name', g.name)), g.parent_group_id
        FROM `group` g INNER JOIN rgroup r ON g.group_id = r.recursive_group_id
        WHERE r.group_id = #{groupId}
    )
    SELECT group_id, parent_group_id, name, `path`
    FROM rgroup
    WHERE recursive_group_id IS NULL
"""

private const val UPDATE_GROUP = """
    UPDATE `group`
    SET parent_group_id=#{parentGroupId}, name=#{name}
    WHERE group_id=#{groupId}
"""

private const val DELETE_GROUP =
    "DELETE FROM `group` WHERE group_id=#{groupId} "

@Mapper
interface GroupDao {

    @Insert(CREATE_GROUP)
    @Options(useGeneratedKeys = true, keyProperty = "groupId", keyColumn = "group_id")
    fun createGroup(groupEntity: GroupEntity): Int

    @Select(READ_GROUP)
    @Result(property = "groupId", column = "group_id")
    @Result(property = "parentGroupId", column = "parent_group_id")
    @Result(property = "name", column = "name")
    fun readGroup(groupId: Int): GroupEntity?

    @Select(READ_GROUPS_BY_PARENT)
    @Result(property = "groupId", column = "group_id")
    @Result(property = "parentGroupId", column = "parent_group_id")
    @Result(property = "name", column = "name")
    fun readGroupsByParent(parentGroupId: Int?): List<GroupEntity>

    @Select(READ_GROUP_AND_PATH)
    @Result(property = "groupId", column = "group_id")
    @Result(property = "parentGroupId", column = "parent_group_id")
    @Result(property = "name", column = "name")
    @Result(property = "path", column = "path")
    fun readGroupPath(groupId: Int): GroupPathEntity?

    @Update(UPDATE_GROUP)
    fun updateGroup(groupEntity: GroupEntity): Int

    @Delete(DELETE_GROUP)
    fun deleteGroup(groupId: Int): Int
}
