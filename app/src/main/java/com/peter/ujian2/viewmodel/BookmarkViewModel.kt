package com.peter.ujian2.viewmodel

import BookmarkPagingSource
import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import android.os.Environment
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.peter.ujian2.model.Idea
import com.peter.ujian2.services.IdeaServices
import com.peter.ujian2.services.NetworkConfig
import com.peter.ujian2.services.ResponseServices
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream

class BookmarkViewModel(application: Application) : AndroidViewModel(application) {

    private val _post = MutableLiveData<ResponseServices>()
    val post: LiveData<ResponseServices> get() = _post

    // Inisialisasi IdeaServices dengan NetworkConfig
    private val ideaServices: IdeaServices = NetworkConfig(application).getIdeaServices()

    private val searchQueryFlow = MutableStateFlow<String?>(null)

    // Menampung PagingData Flow untuk pencarian dan pengguna
    private val _pagingDataFlow: Flow<PagingData<Idea>> = searchQueryFlow
        .flatMapLatest { query ->
            // Ambil userId dari SharedPreferences atau Session
            val userId = getUserId() // Mendapatkan userId dari sumber yang sesuai

            // Gunakan BookmarkPagingSource dengan userId
            Pager(
                config = PagingConfig(pageSize = 5, enablePlaceholders = false),
                pagingSourceFactory = { BookmarkPagingSource(ideaServices, userId, getApplication()) }
            ).flow
        }.cachedIn(viewModelScope)

    val pagingDataFlow: Flow<PagingData<Idea>> get() = _pagingDataFlow

    // Fungsi untuk mendapatkan semua pengguna dengan opsional query pencarian
    fun getIdea(query: String? = null): Flow<PagingData<Idea>> {
        updateSearchQuery(query)
        return pagingDataFlow
    }

    // Fungsi untuk memperbarui query pencarian dan memicu flow
    fun updateSearchQuery(query: String?) {
        searchQueryFlow.value = query
    }

    // Fungsi untuk mendapatkan userId dari SharedPreferences atau session
    private fun getUserId(): Long {
        val sharedPreferences: SharedPreferences = getApplication<Application>().getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
        return sharedPreferences.getLong("user_id", 0L) // Ambil user_id yang tersimpan
    }

    fun downloadFile(fileId: String, callback: (Boolean, String?) -> Unit) {
        viewModelScope.launch {
            try {
                val response = ideaServices.downloadFile(fileId) // Pastikan API mengembalikan nama file
                if (response.isSuccessful) {
                    val inputStream = response.body()?.byteStream()
                    val fileName = response.headers()["Content-Disposition"]?.let { disposition ->
                        Regex("filename=\"(.*)\"").find(disposition)?.groupValues?.get(1)
                    } ?: "file_$fileId.pdf" // Gunakan nama default jika nama file tidak tersedia

                    if (inputStream != null) {
                        // Tentukan folder penyimpanan di Downloads
                        val downloadsDir = File(
                            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),
                            "my_app"
                        )
                        if (!downloadsDir.exists()) {
                            downloadsDir.mkdirs()
                        }

                        // Simpan file dengan nama dari server
                        val file = File(downloadsDir, fileName)
                        val outputStream = FileOutputStream(file)

                        // Menyalin data dari input stream ke file
                        inputStream.copyTo(outputStream)
                        outputStream.close()

                        // Memastikan file sudah disalin dengan sukses
                        callback(true, "Download sukses! Lokasi file: ${file.absolutePath}")
                    } else {
                        callback(false, "File tidak ditemukan.")
                    }
                } else {
                    callback(false, response.message())
                }
            } catch (e: Exception) {
                callback(false, e.message)
            }
        }
    }
}
