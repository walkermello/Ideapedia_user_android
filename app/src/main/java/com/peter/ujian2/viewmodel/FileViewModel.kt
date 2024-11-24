package com.peter.ujian2.viewmodel

import android.app.Application
import android.content.ContentValues
import android.content.Context.MODE_PRIVATE
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.peter.ujian2.model.DetailIdea
import com.peter.ujian2.model.Idea
import com.peter.ujian2.pagination.IdeaPagingSource
import com.peter.ujian2.services.IdeaServices
import com.peter.ujian2.services.NetworkConfig
import com.peter.ujian2.services.ResponseServices
import com.peter.ujian2.utils.Constants.BASE_URL
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.RequestBody
import retrofit2.Retrofit
import java.io.File
import java.io.FileOutputStream

class FileViewModel(application: Application) : AndroidViewModel(application) {

    enum class UploadStatus {
        LOADING,
        SUCCESS,
        ERROR
    }

    private val _post = MutableLiveData<ResponseServices>()
    val post: LiveData<ResponseServices> get() = _post

    private val _uploadStatus = MutableLiveData<UploadStatus>()
    val uploadStatus: LiveData<UploadStatus> get() = _uploadStatus

    private val _getIdea = MutableLiveData<DetailIdea>()
    val getIdea: LiveData<DetailIdea> get() = _getIdea

    // Inisialisasi IdeaServices dengan NetworkConfig
    private val ideaServices: IdeaServices = NetworkConfig(application).getIdeaServices()

    private val searchQueryFlow = MutableStateFlow<String?>(null)

    // Menampung PagingData Flow untuk pencarian dan pengguna
    private val _pagingDataFlow: Flow<PagingData<DetailIdea>> = searchQueryFlow
        .flatMapLatest { query ->
            Pager(
                config = PagingConfig(pageSize = 5, enablePlaceholders = false),
                pagingSourceFactory = { IdeaPagingSource(ideaServices, query) }
            ).flow
        }.cachedIn(viewModelScope)

    val pagingDataFlow: Flow<PagingData<DetailIdea>> get() = _pagingDataFlow

    // Fungsi untuk mendapatkan semua pengguna dengan opsional query pencarian
    fun getIdea(query: String? = null): Flow<PagingData<DetailIdea>> {
        updateSearchQuery(query)
        return pagingDataFlow
    }

    // Fungsi untuk memperbarui query pencarian dan memicu flow
    fun updateSearchQuery(query: String?) {
        searchQueryFlow.value = query
        Log.d("FileViewModel", "Query di ViewModel: $query")
    }

    // Fungsi untuk mencari pengguna berdasarkan username
    fun searchIdeaByUsername(username: String) {
        updateSearchQuery(username)
        Log.d("FileViewModel", "Query di ViewModel untuk username: $username")
    }

    // Fungsi untuk mengupload file dengan status upload
    fun uploadFile(
        judul: RequestBody,
        deskripsi: RequestBody,
        file: MultipartBody.Part?,
        image: MultipartBody.Part?
    ) {
        viewModelScope.launch {
            _uploadStatus.postValue(UploadStatus.LOADING)
            try {
                val response = ideaServices.uploadFile(judul, deskripsi, file, image)
                if (response.isSuccessful) {
                    _uploadStatus.postValue(UploadStatus.SUCCESS)
                    Log.d("FileViewModel", "Upload berhasil")
                } else {
                    _uploadStatus.postValue(UploadStatus.ERROR)
                    Log.e("FileViewModel", "Upload gagal: ${response.message()}")
                }
            } catch (e: Exception) {
                _uploadStatus.postValue(UploadStatus.ERROR)
                Log.e("FileViewModel", "Error upload: ${e.message}")
            }
        }
    }

    // Mengambil Bearer Token dari SharedPreferences
    private fun getBearerToken(): String {
        val sharedPreferences = getApplication<Application>().getSharedPreferences("app_prefs", MODE_PRIVATE)
        val token = sharedPreferences.getString("bearer_token", "") ?: ""
        Log.d("FileViewModel", "Bearer Token: $token")  // Log token
        return token
    }

    // Fungsi untuk upload file dengan callback (opsional)
    fun uploadFileWithCallback(
        judul: RequestBody,
        deskripsi: RequestBody,
        file: MultipartBody.Part?,
        image: MultipartBody.Part?,
        callback: (UploadStatus, String?) -> Unit
    ) {
        viewModelScope.launch {
            callback(UploadStatus.LOADING, null)
            try {
                val response = ideaServices.uploadFile(judul, deskripsi, file, image)
                if (response.isSuccessful) {
                    callback(UploadStatus.SUCCESS, "Upload berhasil")
                } else {
                    callback(UploadStatus.ERROR, response.message())
                }
            } catch (e: Exception) {
                callback(UploadStatus.ERROR, e.message)
            }
        }
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
