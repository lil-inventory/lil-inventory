package org.ivcode.inventory.service

import org.ivcode.inventory.repository.SearchDao
import org.ivcode.inventory.repository.model.PageInfo
import org.ivcode.inventory.service.model.AssetSearchResults
import org.ivcode.inventory.util.toAssetNavInfo
import org.springframework.stereotype.Service

@Service
class SearchService(
    private val searchDao: SearchDao
) {

    fun searchAssetsWithPagination(query:String, page: Int, pageSize: Int): AssetSearchResults {
        val pageResults = searchDao.searchAssets(keyword = query, PageInfo(offset=page*pageSize, size=pageSize))
        val total = searchDao.foundRows()

        return AssetSearchResults(
            page = page,
            pageSize = pageSize,
            totalPages = total / pageSize,
            assets = pageResults.map { it.toAssetNavInfo() }
        )
    }
}