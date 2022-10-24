package org.ivcode.inventory.service.model

data class AssetSearchResults (
    val page: Int,
    val pageSize: Int,
    val totalPages: Int,
    val assets: List<AssetNavInfo>
)