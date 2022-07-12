package org.ivcode.inventory.controller

import org.ivcode.inventory.service.SearchService
import org.ivcode.inventory.service.model.AssetSearchResults
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/search")
class SearchController(
    val searchService: SearchService
) {

    @GetMapping("/assets")
    fun searchAssets (
        @RequestParam query: String,
        @RequestParam page: Int,
        @RequestParam pageSize: Int
    ): AssetSearchResults =
        searchService.searchAssetsWithPagination(query, page, pageSize)
}