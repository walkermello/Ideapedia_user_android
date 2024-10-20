package com.peter.ujian2

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import com.peter.ujian2.viewmodel.UserViewModel
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody

class EditUser : AppCompatActivity() {

    private lateinit var viewModel: UserViewModel
    private lateinit var editTextNama: EditText
    private lateinit var editTextAlamat: EditText
    private lateinit var editTextHutang: EditText
    private lateinit var btnUpdate: Button

    private var userId: Int? = null // Simpan ID pengguna di sini

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_edit_user)

        // Inisialisasi elemen UI
        editTextNama = findViewById(R.id.editTextNama)
        editTextAlamat = findViewById(R.id.editTextAlamat)
        editTextHutang = findViewById(R.id.editTextHutang)
        btnUpdate = findViewById(R.id.btnUpdate)

        // Ambil ID pengguna dari intent
        userId = intent.getIntExtra("USER_ID", -1)

        // Ambil data pengguna dan set ke EditText
        editTextNama.setText(intent.getStringExtra("USER_NAMA"))
        editTextAlamat.setText(intent.getStringExtra("USER_ALAMAT"))
        editTextHutang.setText(intent.getStringExtra("USER_HUTANG"))

        // Inisialisasi ViewModel
        viewModel = ViewModelProvider(this).get(UserViewModel::class.java)

        // Set listener untuk tombol update
        btnUpdate.setOnClickListener {
            updateUser()
        }
    }

    private fun updateUser() {
        val nama = editTextNama.text.toString()
        val alamat = editTextAlamat.text.toString()
        val hutang = editTextHutang.text.toString().toIntOrNull()

        // Periksa ID valid
        if (userId != null && hutang != null) {
            viewModel.updateUser(userId!!, nama, alamat, hutang)
            viewModel.updateUser.observe(this) { response ->
                if (response != null) {
                    // Tampilkan pesan sukses atau lakukan tindakan lain
                    Toast.makeText(this, "Pengguna berhasil diperbarui", Toast.LENGTH_SHORT).show()
                    finish() // Kembali ke aktivitas sebelumnya
                }
            }
        } else {
            Toast.makeText(this, "ID pengguna tidak valid atau hutang tidak valid", Toast.LENGTH_SHORT).show()
        }
    }
}

