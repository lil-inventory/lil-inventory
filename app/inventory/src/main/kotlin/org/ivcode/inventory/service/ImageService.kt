package org.ivcode.inventory.service

import org.ivcode.inventory.common.exception.NotFoundException
import org.ivcode.inventory.repository.ImageDao
import org.ivcode.inventory.repository.model.ImageInfoEntity
import org.ivcode.inventory.service.model.ImageInfo
import org.ivcode.inventory.util.toImageInfo
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.io.InputStream
import java.io.OutputStream

@Service
class ImageService(
    val imageDao: ImageDao
) {

    @Transactional
    fun addImage (
        assetId: Int,
        filename: String,
        mime: String,
        data: InputStream
    ): ImageInfo {
        val entity = ImageInfoEntity (
            assetId = assetId,
            filename = filename,
            mime = mime
        )
        imageDao.createImage(entity, data)
        return entity.toImageInfo()
    }

    @Transactional
    fun getImageInfo(imageId: Int): ImageInfo {
        val entity = imageDao.readImageInfo(imageId) ?: throw NotFoundException()
        return entity.toImageInfo()
    }


    @Transactional
    fun getImageData(imageId: Int, out: OutputStream) {
        val input = imageDao.readImageData(imageId) ?: throw NotFoundException()
        input.use {
            it.transferTo(out)
        }
    }

    @Transactional
    fun deleteImage(imageId: Int) {
        val count = imageDao.deleteImage(imageId)
        if(count==0) {
            throw NotFoundException()
        }
    }
}