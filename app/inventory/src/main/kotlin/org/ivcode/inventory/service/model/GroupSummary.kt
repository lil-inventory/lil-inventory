package org.ivcode.inventory.service.model

import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "A group summary")
data class GroupSummary (

    @Schema(description = "Group Identifier")
    val groupId: Long,

    @Schema(description = "Inventory Identifier")
    val inventoryId: Long,

    @Schema(description = "Group Name")
    val name: String,

    val parentGroupId: Long?
)
