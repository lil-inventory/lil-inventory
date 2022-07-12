package org.ivcode.inventory.repository

import org.apache.ibatis.annotations.*
import org.ivcode.inventory.repository.model.ImageInfoEntity
import java.io.InputStream

private const val CREATE_IMAGE = """
    INSERT INTO `image` (asset_id, filename, mime, data)
    VALUES (#{imageInfo.assetId}, #{imageInfo.filename}, #{imageInfo.mime}, #{data})
"""

private const val READ_IMAGE_INFO =
    "SELECT image_id, asset_id, filename, mime FROM `image` WHERE image_id=#{imageId}"

private const val READ_IMAGE_DATA =
    "SELECT data FROM `image` WHERE image_id=#{imageId}"

private const val READ_ASSET_IMAGE_DATA =
    "SELECT image_id, asset_id, filename, mime FROM `image` WHERE asset_id=#{assetId}"

private const val DELETE_IMAGE =
    "DELETE FROM `image` WHERE image_id=#{imageId}"

@Mapper
interface ImageDao {

    @Insert(CREATE_IMAGE)
    @Options(useGeneratedKeys = true, keyProperty = "imageInfo.imageId", keyColumn = "image_id")
    fun createImage(imageInfo: ImageInfoEntity, data: InputStream): Int

    @Select(READ_IMAGE_INFO)
    @Result(property = "imageId", column = "image_id")
    @Result(property = "assetId", column = "asset_id")
    @Result(property = "filename", column = "filename")
    @Result(property = "mime", column = "mime")
    fun readImageInfo(imageId: Int): ImageInfoEntity?

    @Select(READ_ASSET_IMAGE_DATA)
    @Result(property = "imageId", column = "image_id")
    @Result(property = "assetId", column = "asset_id")
    @Result(property = "filename", column = "filename")
    @Result(property = "mime", column = "mime")
    fun readAssetImageInfo(assetId: Int): List<ImageInfoEntity>

    @Select(READ_IMAGE_DATA)
    fun readImageData(imageId: Int): InputStream?

    @Delete(DELETE_IMAGE)
    fun deleteImage(imageId: Int): Int
}