package org.ivcode.inventory.service.model

data class ImageInfo (
    val imageId: Int,
    val assetId: Int,
    val filename: String,
    val mime: String
)