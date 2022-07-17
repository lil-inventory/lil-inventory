package org.ivcode.inventory.service.model

data class ImageInfo (
    val imageId: Long,
    val assetId: Long,
    val filename: String,
    val mime: String
)