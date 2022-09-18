package org.ivcode.inventory.service.model

data class NavigationElement (
    val inventoryId: Long?,
    val groupId: Long?,
    val name: String,
    val path: List<GroupPathElement>,
    val groups: List<GroupSummary>,
    val assets: List<AssetSummary>
)
