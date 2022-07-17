package org.ivcode.inventory.controller

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import org.ivcode.inventory.service.ImageService
import org.ivcode.inventory.service.model.ImageInfo
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody

@RestController
@RequestMapping("/image")
class ImageController(
    val imageService: ImageService
) {

    @GetMapping("/{imageId}/image")
    @Operation(
        responses = [ApiResponse(description = "image data", content = [Content(schema = Schema(hidden = true))])])
    fun readImage(@PathVariable imageId: Long): ResponseEntity<StreamingResponseBody> {
        val image = imageService.getImageInfo(imageId)

        val stream = StreamingResponseBody { out ->
            imageService.getImageData(imageId, out)
        }

        return ResponseEntity
            .ok()
            .contentType(MediaType.parseMediaType(image.mime))
            .body(stream)
    }

    @GetMapping("/{imageId}/metadata")
    fun readImageInfo(@PathVariable imageId: Long): ImageInfo =
        imageService.getImageInfo(imageId)

    @PostMapping(consumes=[MediaType.MULTIPART_FORM_DATA_VALUE])
    fun postImage(
        @RequestParam("" +
                "") assetId: Long,
        @RequestParam("image") multipartFile: MultipartFile
    ): ImageInfo = imageService.addImage(
        assetId = assetId,
        filename = multipartFile.originalFilename,
        mime = multipartFile.contentType,
        data = multipartFile.inputStream
    )

    @DeleteMapping("/{imageId}")
    fun deleteImage (@PathVariable imageId: Long) =
        imageService.deleteImage(imageId)
}