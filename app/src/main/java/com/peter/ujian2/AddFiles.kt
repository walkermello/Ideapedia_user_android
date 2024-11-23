package com.peter.ujian2

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.OpenableColumns
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.peter.ujian2.viewmodel.FileViewModel
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody


class AddFiles : AppCompatActivity() {
    private lateinit var etTitle: TextInputEditText
    private lateinit var etDescription: TextInputEditText
    private lateinit var tvFileName: TextView
    private lateinit var btnChooseFile: MaterialButton
    private lateinit var btnSubmit: MaterialButton
    private lateinit var btnBack: ImageView
    private var selectedFileUri: Uri? = null

    //inisiasi pembuatan acc user baru
    private val viewModel: FileViewModel by viewModels()

    fun initComponent(){
        enableEdgeToEdge()
        etTitle = findViewById(R.id.etTitle)
        etDescription = findViewById(R.id.etDescription)
        tvFileName = findViewById(R.id.tvFileName)
        btnChooseFile = findViewById(R.id.btnChooseFile)
        btnSubmit = findViewById(R.id.btnSubmit)
        btnBack = findViewById(R.id.btnBack)

        //btnChooseFile.setOnClickListener { chooseFile() }
        //btnSubmit.setOnClickListener { submitData() }

        // Set up the back button click listener
        btnBack.setOnClickListener {
            super.onBackPressed()
        }
    }


//    private fun chooseFile() {
//        val intent = Intent(Intent.ACTION_GET_CONTENT)
//        intent.type = "*/*"
//        startActivityForResult(intent, REQUEST_CODE_PICK_FILE)
//    }

//    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
//        super.onActivityResult(requestCode, resultCode, data)
//        if (requestCode == REQUEST_CODE_PICK_FILE && resultCode == Activity.RESULT_OK) {
//            selectedFileUri = data?.data
//            selectedFileUri?.let { tvFileName.text = getFileName(it) }
//        }
//    }

//    private fun getFileName(uri: Uri): String {
//        var fileName = "Unknown"
//        contentResolver.query(uri, null, null, null, null)?.use { cursor ->
//            if (cursor.moveToFirst()) {
//                fileName = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME))
//            }
//        }
//        return fileName
//    }

//    fun submitData(){
//
//        val title = etTitle.text.toString()
//        val description = etDescription.text.toString()
//
//        if (title.isEmpty() || description.isEmpty() || selectedFileUri == null) {
//            Toast.makeText(this, "Please fill all fields and choose a file", Toast.LENGTH_SHORT).show()
//            return
//        }
//
//        if (title.isNotEmpty() && description.isNotEmpty() && selectedFileUri.isNotEmpty()){
//
//            // Menggunakan metode terbaru untuk RequestBody
//            val rbTitle = RequestBody.create("text/plain".toMediaTypeOrNull(), title)
//            val rbDescription = RequestBody.create("text/plain".toMediaTypeOrNull(), description)
//            val rbSelectedFileUri = RequestBody.create("text/plain".toMediaTypeOrNull(), selectedFileuri.toString())
//
//            viewModel.postUser(rbTitle, rbDescription, rbSelectedFileUri)
//        }else{
//            Toast.makeText(this, "Data belum lengkap", Toast.LENGTH_SHORT).show()
//        }
//
//    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_user)
        // Inisialisasi komponen UI
        initComponent()

        // Mengamati hasil _post dari viewModel
        viewModel.post.observe(this) { response ->
            if (response != null && response.status == true) {
                finish() // Menutup activity setelah berhasil
            } else {
                Toast.makeText(this, "Failed to Upload Data", Toast.LENGTH_SHORT).show()
            }
        }

        viewModel.post.observe(this){
            if (it.status == true){
                finish()
            }
        }
    }
}