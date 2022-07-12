package org.ivcode.inventory.service.model

data class NavigationElement (
    val groupId: Int?,
    val name: String,
    val path: List<GroupPathElement>,
    val groups: List<GroupSummary>,
    val assets: List<AssetSummary>
)
