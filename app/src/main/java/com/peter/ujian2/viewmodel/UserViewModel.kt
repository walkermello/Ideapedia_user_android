package com.peter.ujian2.viewmodel

import android.app.Application
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.peter.ujian2.model.User
import com.peter.ujian2.services.NetworkConfig
import com.peter.ujian2.services.ResponseServices
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

    // Constructor sudah otomatis mendapatkan `application`
    private val appContext = application.applicationContext

    fun postUser(nama: RequestBody, alamat: RequestBody, hutang: RequestBody) {
        NetworkConfig().getServiceUser().addUser(nama, alamat, hutang).enqueue(object : retrofit2.Callback<ResponseServices> {
            override fun onResponse(call: Call<ResponseServices>, response: Response<ResponseServices>) {
                if (response.isSuccessful) {
                    _post.postValue(response.body())
                } else {
                    Toast.makeText(appContext, "Error: ${response.message()}", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<ResponseServices>, t: Throwable) {
                Toast.makeText(appContext, "Gagal Mengupload Data: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    fun getUser() {
        NetworkConfig().getServiceUser().getAllUser().enqueue(object : retrofit2.Callback<User> {
            override fun onResponse(call: Call<User>, response: Response<User>) {
                if (response.isSuccessful) {
                    _getUser.postValue(response.body())
                } else {
                    Toast.makeText(appContext, "Error: ${response.message()}", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<User>, t: Throwable) {
                Toast.makeText(appContext, "Gagal mengambil data pengguna: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    fun updateUser(userId: Int, nama: String, alamat: String, hutang: Int) {
        NetworkConfig().getServiceUser().updateUser(userId, nama, alamat, hutang)
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
        NetworkConfig().getServiceUser().deleteUser(requestBody)
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
