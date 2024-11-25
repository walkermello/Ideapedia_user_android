package com.peter.ujian2.pagination

import android.content.Context
import android.util.Log
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.peter.ujian2.model.HistoryItem  // Assuming HistoryItem is the correct model
import com.peter.ujian2.services.IdeaServices
import retrofit2.HttpException
import java.io.IOException

class HistoryPagingSource(
    private val ideaService: IdeaServices,  // Konstruktornya
    private val userId: Long,  // Menambahkan userId untuk mendapatkan history berdasarkan user
    private val context: Context  // Menambahkan context untuk SharedPreferences
) : PagingSource<Long, HistoryItem>() {  // Change to HistoryItem

    override suspend fun load(params: LoadParams<Long>): LoadResult<Long, HistoryItem> {
        val start = params.key ?: 0L  // Mulai dari halaman pertama jika null
        val limit = params.loadSize  // Jumlah item per halaman

        return try {
            // Ambil token dari SharedPreferences
            val token = getToken()
            Log.d("API Request", "Calling getHistory with userId: $userId and token: $token")
            // Jika token ada, tambahkan ke header
            val response = ideaService.getHistory(userId, "Bearer $token")

            if (response.isSuccessful) {
                // Mengakses data 'history' jika respons berhasil
                val historyData = response.body() ?: emptyList() // Replace with correct response format

                // Map the history data to a list of HistoryItem
                val historyItems = historyData.map { HistoryItem(
                    id = it.id,
                    date = it.createdAt,
                    title = it.idea.judul,
                    profile = it.user.username,
                    action = it.action,
                    status = "completed"  // Adjust as necessary based on your data
                ) }

                LoadResult.Page(
                    data = historyItems,  // Menggunakan data HistoryItem
                    prevKey = if (start == 0L) null else start - limit,
                    nextKey = if (historyItems.size < limit) null else start + limit
                )
            } else {
                LoadResult.Error(HttpException(response))
            }
        } catch (e: IOException) {
            LoadResult.Error(e)
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }

    // Fungsi untuk mengambil token dari SharedPreferences
    private fun getToken(): String? {
        val sharedPreferences = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        return sharedPreferences.getString("bearer_token", null)
    }

    // Menentukan halaman refresh
    override fun getRefreshKey(state: PagingState<Long, HistoryItem>): Long? {
        return state.anchorPosition?.let { anchor ->
            state.closestPageToPosition(anchor)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchor)?.nextKey?.minus(1)
        }
    }

    // Mendukung penggunaan kembali key
    override val keyReuseSupported: Boolean
        get() = true
}
