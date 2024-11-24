package com.peter.ujian2.pagination

import android.util.Log
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.peter.ujian2.model.DetailIdea
import com.peter.ujian2.services.IdeaServices
import retrofit2.HttpException
import java.io.IOException

class IdeaPagingSource(
    private val ideaService: IdeaServices,
    private var searchQuery: String? = null
) : PagingSource<Int, DetailIdea>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, DetailIdea> {
        val start = params.key ?: 0
        val limit = params.loadSize
        val sortOrder = "asc"
        val sortField = "id"

        return try {
            val response = if (searchQuery.isNullOrEmpty()) {
                ideaService.getIdeasByStatusApproved(
                    start = start,
                    sortOrder = sortOrder,
                    sortField = sortField,
                    size = limit
                )
            } else {
                val query = "%${searchQuery}%"
                ideaService.getIdeasByParams(
                    size = limit,
                    col = "status",
                    value = "Approved",
                    start = start,
                    sortOrder = sortOrder,
                    sortField = sortField,
                    username = query,
                    title = query,
                    description = query
                )
            }

            if (response.isSuccessful) {
                val ideaData = response.body()?.content ?: emptyList()
                Log.d("IdeaPagingSource", "Page loaded: start=$start, limit=$limit, dataSize=${ideaData.size}")

                LoadResult.Page(
                    data = ideaData,
                    prevKey = if (start == 0) null else start - limit,
                    nextKey = if (ideaData.size < limit) null else start + limit
                )
            } else {
                Log.e("IdeaPagingSource", "Response failed: ${response.code()} - ${response.message()}")
                LoadResult.Error(HttpException(response))
            }
        } catch (e: IOException) {
            Log.e("IdeaPagingSource", "IO Exception: ${e.message}")
            LoadResult.Error(e)
        } catch (e: Exception) {
            Log.e("IdeaPagingSource", "Unexpected Exception: ${e.message}")
            LoadResult.Error(e)
        }
    }

    fun setSearchQuery(query: String?) {
        searchQuery = query
        invalidate() // Trigger refresh
    }

    override fun getRefreshKey(state: PagingState<Int, DetailIdea>): Int? {
        return state.anchorPosition?.let { anchor ->
            state.closestPageToPosition(anchor)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchor)?.nextKey?.minus(1)
        }
    }

    override val keyReuseSupported: Boolean
        get() = true
}
