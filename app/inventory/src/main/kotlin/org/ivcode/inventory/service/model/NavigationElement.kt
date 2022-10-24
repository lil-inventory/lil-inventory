package org.ivcode.inventory.service.model

data class NavigationElement (
    val inventory: InventoryNavInfo,
    val group: GroupNavInfo?,
    val path: List<GroupPathElement>,
    val childGroups: List<GroupNavInfo>,
    val childAssets: List<AssetNavInfo>
)
