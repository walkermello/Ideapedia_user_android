//package com.peter.ujian2.pagination
//
//import android.util.Log
//import androidx.paging.PagingSource
//import androidx.paging.PagingState
//import com.peter.ujian2.model.UserItem
//import com.peter.ujian2.services.UserServices
//import retrofit2.HttpException
//import java.io.IOException
//
//class UserPagingSource(
//    private val userService: UserServices,
//    private var searchQuery: String? = null // Tambahkan searchQuery
//) : PagingSource<Int, UserItem>() {
//
//    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, UserItem> {
//        val start = params.key ?: 0
//        val limit = params.loadSize
//
//        return try {
//            // Panggil API dengan query pencarian yang sesuai
//            val response = if (searchQuery.isNullOrEmpty()) {
//                // Jika tidak ada query pencarian, panggil API tanpa filter
//                userService.getAllUser(start = start, limit = limit)
//            } else {
//                // Jika ada query pencarian, panggil API dengan filter
//                userService.getUserByName(
//                    value = "%$searchQuery%", // Menggunakan wildcard untuk pencarian
//                    start = start,
//                    limit = limit
//                )
//            }
//
//            if (response.isSuccessful) {
//                val userData = response.body()?.data?.newUser?.filterNotNull() ?: emptyList()
//
//                LoadResult.Page(
//                    data = userData,
//                    prevKey = if (start == 0) null else start - limit,
//                    nextKey = if (userData.size < limit) null else start + limit // Jika data yang diterima kurang dari limit, tidak ada halaman berikutnya
//                )
//
//            } else {
//                LoadResult.Error(HttpException(response))
//            }
//        } catch (exception: IOException) {
//            LoadResult.Error(exception)
//        } catch (exception: HttpException) {
//            LoadResult.Error(exception)
//        } catch (exception: Exception) {
//            LoadResult.Error(exception)
//        }
//    }
//
//    fun setSearchQuery(query: String?) {
//        searchQuery = query
//        Log.d("UserPagingSource", "Query di PagingSource: $query")
//        invalidate() // Menandai PagingSource sebagai tidak valid sehingga data dapat dimuat ulang
//    }
//
//    override fun getRefreshKey(state: PagingState<Int, UserItem>): Int? {
//        return null
//    }
//
//    override val keyReuseSupported: Boolean
//        get() = true
//}
//
