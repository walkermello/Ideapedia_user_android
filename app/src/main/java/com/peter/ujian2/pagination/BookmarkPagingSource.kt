import android.content.Context
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.peter.ujian2.model.DetailIdea
import com.peter.ujian2.model.Idea
import com.peter.ujian2.services.IdeaServices
import retrofit2.HttpException
import java.io.IOException

class BookmarkPagingSource(
    private val ideaService: IdeaServices,  // Konstruktornya
    private val userId: Long,  // Menambahkan userId untuk mendapatkan bookmark berdasarkan user
    private val context: Context, // Menambahkan context untuk SharedPreferences
    private var searchQuery: String? = null // Query pencarian jika ada
) : PagingSource<Long, Idea>() {  // Ubah dari DetailIdea menjadi Idea

    override suspend fun load(params: LoadParams<Long>): LoadResult<Long, Idea> {
        val start = params.key ?: 0L  // Mulai dari halaman pertama jika null
        val limit = params.loadSize  // Jumlah item per halaman

        return try {
            // Ambil token dari SharedPreferences
            val token = getToken()

            // Jika token ada, tambahkan ke header
            val response = ideaService.getBookmarkIdea(userId, "Bearer $token")

            if (response.isSuccessful) {
                // Mengakses array langsung, tidak ada field `content`
                val bookmarkData = response.body() ?: emptyList() // Respons berupa array Bookmark

                // Mengambil hanya field `idea` yang berisi data Idea langsung
                val ideaData = bookmarkData.map { it.idea }  // Map Bookmark ke Idea (mengambil field `idea`)

                LoadResult.Page(
                    data = ideaData,  // Menggunakan data Idea langsung
                    prevKey = if (start == 0L) null else start - limit,
                    nextKey = if (ideaData.size < limit) null else start + limit
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

    // Fungsi untuk memperbarui query pencarian
    fun setSearchQuery(query: String?) {
        searchQuery = query
        invalidate() // Memicu refresh data
    }

    // Menentukan halaman refresh
    override fun getRefreshKey(state: PagingState<Long, Idea>): Long? {
        return state.anchorPosition?.let { anchor ->
            state.closestPageToPosition(anchor)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchor)?.nextKey?.minus(1)
        }
    }

    // Mendukung penggunaan kembali key
    override val keyReuseSupported: Boolean
        get() = true
}
