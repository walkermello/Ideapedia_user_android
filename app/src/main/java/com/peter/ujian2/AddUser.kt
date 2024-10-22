package com.peter.ujian2

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.peter.ujian2.viewmodel.UserViewModel
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody

class AddUser : AppCompatActivity() {
    lateinit var txtNama : EditText
    lateinit var txtAlamat : EditText
    lateinit var txtHutang : EditText
    lateinit var btnUpload : Button

    //inisiasi pembuatan acc user baru
    private val viewModel: UserViewModel by viewModels()

    fun initComponent(){
        txtNama = findViewById(R.id.editTextNama)
        txtAlamat = findViewById(R.id.editTextAlamat)
        txtHutang = findViewById(R.id.editTextHutang)
        btnUpload = findViewById(R.id.btnUpload)

        btnUpload.setOnClickListener{
            sendData()
        }

    }

    fun sendData(){

        val nama = txtNama.text.toString()
        val alamat = txtAlamat.text.toString()
        val hutang = txtHutang.text.toString()

        if (nama.isNotEmpty() && alamat.isNotEmpty() && hutang.isNotEmpty()){

            // Menggunakan metode terbaru untuk RequestBody
            val rbNama = RequestBody.create("text/plain".toMediaTypeOrNull(), nama)
            val rbAlamat = RequestBody.create("text/plain".toMediaTypeOrNull(), alamat)
            val rbHutang = RequestBody.create("text/plain".toMediaTypeOrNull(), hutang)

            viewModel.postUser(rbNama, rbAlamat, rbHutang)
        }else{
            Toast.makeText(this, "Data belum lengkap", Toast.LENGTH_SHORT).show()
        }

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_add_user)
        // Inisialisasi komponen UI
        initComponent()

        // Mengamati hasil _post dari viewModel
        viewModel.post.observe(this) { response ->
            if (response != null && response.status == true) {
                finish() // Menutup activity setelah berhasil
            } else {
                Toast.makeText(this, "Gagal menambahkan user. Coba lagi.", Toast.LENGTH_SHORT).show()
            }
        }

        viewModel.post.observe(this){
            if (it.status == true){
                finish()
            }
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }
}