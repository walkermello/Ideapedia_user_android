package com.peter.ujian2.viewmodel

import android.app.Application
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.peter.ujian2.model.User
import com.peter.ujian2.model.UserItem
import com.peter.ujian2.pagination.UserPagingSource
import com.peter.ujian2.services.NetworkConfig
import com.peter.ujian2.services.ResponseServices
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.launch
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class UserViewModel(application: Application) : AndroidViewModel(application) {

    private val _post = MutableLiveData<ResponseServices>()
    val post: LiveData<ResponseServices> get() = _post

    private val _getUser = MutableLiveData<User>()
    val getUser: LiveData<User> get() = _getUser

    private val _updateUser = MutableLiveData<ResponseServices>()
    val updateUser: LiveData<ResponseServices> get() = _updateUser

    private val _deleteUserSuccess = MutableLiveData<Boolean>()
    val deleteUserSuccess: LiveData<Boolean> get() = _deleteUserSuccess

    // Inisialisasi userServices
    private val userServices = NetworkConfig().getServiceUser()

    // StateFlow untuk query pencarian
    private val searchQueryFlow = MutableStateFlow<String?>(null)

    // Menampung PagingData Flow untuk pencarian dan pengguna
    private val _pagingDataFlow: Flow<PagingData<UserItem>> = searchQueryFlow
        .flatMapLatest { query ->
            Pager(
                config = PagingConfig(pageSize = 5, enablePlaceholders = false),
                pagingSourceFactory = { UserPagingSource(userServices, query) }
            ).flow
        }.cachedIn(viewModelScope)

    // Public accessor untuk PagingData
    val pagingDataFlow: Flow<PagingData<UserItem>> get() = _pagingDataFlow

    // Constructor sudah otomatis mendapatkan `application`
    private val appContext = application.applicationContext

    // Fungsi untuk mendapatkan semua pengguna dengan opsional query pencarian
    fun getUser(query: String? = null): Flow<PagingData<UserItem>> {
        searchQueryFlow.value = query // Set nilai query pencarian
        return _pagingDataFlow // Mengembalikan flow PagingData yang sudah diatur
    }

    // Fungsi untuk mencari pengguna berdasarkan nama
    fun searchUserByName(name: String) {
        // Memperbarui query pencarian
        updateSearchQuery(name)
        Log.d("UserViewModel2", "Query di ViewModel: $name")
    }

    fun postUser(nama: RequestBody, alamat: RequestBody, hutang: RequestBody) {
        userServices.addUser(nama, alamat, hutang).enqueue(object : Callback<ResponseServices> {
            override fun onResponse(call: Call<ResponseServices>, response: Response<ResponseServices>) {
                if (response.isSuccessful) {
                    _post.postValue(response.body())
                    Toast.makeText(appContext, "Data pengguna berhasil ditambahkan", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(appContext, "Error: ${response.message()}", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<ResponseServices>, t: Throwable) {
                Toast.makeText(appContext, "Gagal Mengupload Data: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    fun updateSearchQuery(query: String?) {
        // Memperbarui query pencarian dan trigger flow
        searchQueryFlow.value = query
        Log.d("UserViewModel1", "Query di ViewModel: $query")
    }

    fun updateUser(userId: Int, nama: String, alamat: String, hutang: Int) {
        userServices.updateUser(userId, nama, alamat, hutang)
            .enqueue(object : Callback<ResponseServices> {
                override fun onResponse(call: Call<ResponseServices>, response: Response<ResponseServices>) {
                    if (response.isSuccessful) {
                        _updateUser.postValue(response.body())
                        Toast.makeText(appContext, "Data pengguna berhasil diupdate", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(appContext, "Error: ${response.message()}", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<ResponseServices>, t: Throwable) {
                    Toast.makeText(appContext, "Gagal mengupdate data pengguna: ${t.message}", Toast.LENGTH_SHORT).show()
                }
            })
    }

    fun deleteUser(id: Int) {
        val requestBody = mapOf("id" to id) // Mengemas ID ke dalam Map
        userServices.deleteUser(requestBody)
            .enqueue(object : Callback<ResponseServices> {
                override fun onResponse(call: Call<ResponseServices>, response: Response<ResponseServices>) {
                    if (response.isSuccessful) {
                        _deleteUserSuccess.postValue(true) // Berhasil dihapus
                        Toast.makeText(appContext, "Data pengguna berhasil dihapus", Toast.LENGTH_SHORT).show()
                    } else {
                        _deleteUserSuccess.postValue(false) // Gagal dihapus
                        Toast.makeText(appContext, "Error: ${response.message()}", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<ResponseServices>, t: Throwable) {
                    _deleteUserSuccess.postValue(false) // Gagal dihapus
                    Toast.makeText(appContext, "Gagal menghapus data pengguna: ${t.message}", Toast.LENGTH_SHORT).show()
                }
            })
    }
}
