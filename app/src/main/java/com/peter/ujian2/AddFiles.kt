package com.peter.ujian2

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.OpenableColumns
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.peter.ujian2.viewmodel.FileViewModel
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File
import java.io.FileOutputStream

class AddFiles : AppCompatActivity() {

    private lateinit var etTitle: TextInputEditText
    private lateinit var etDescription: TextInputEditText
    private lateinit var tvFileName: TextView
    private lateinit var previewImage: ImageView
    private lateinit var btnChooseFile: MaterialButton
    private lateinit var btnSubmit: MaterialButton
    private lateinit var btnBack: ImageView
    private var selectedFileUri: Uri? = null
    private var selectedImageUri: Uri? = null

    private val viewModel: FileViewModel by viewModels()

    // Launchers for file and image selection
    private val chooseFileLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let { handleFileSelection(it) }
    }

    private val chooseImageLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let { handleImageSelection(it) }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_add_user)

        initComponent()
        observeViewModel()
    }

    private fun initComponent() {
        etTitle = findViewById(R.id.etTitle)
        etDescription = findViewById(R.id.etDescription)
        tvFileName = findViewById(R.id.tvFileName)
        previewImage = findViewById(R.id.previewImage)
        btnChooseFile = findViewById(R.id.btnChooseFile)
        btnSubmit = findViewById(R.id.btnSubmit)
        btnBack = findViewById(R.id.btnBack)

        btnChooseFile.setOnClickListener { chooseFile() }
        previewImage.setOnClickListener { chooseImage() }
        btnSubmit.setOnClickListener { submitData() }
        btnBack.setOnClickListener { onBackPressed() }
    }

    private fun chooseFile() {
        chooseFileLauncher.launch("application/*")
    }

    private fun chooseImage() {
        chooseImageLauncher.launch("image/*")
    }

    private fun handleFileSelection(uri: Uri) {
        val mimeType = contentResolver.getType(uri)
        if (mimeType in listOf(
                "application/pdf",
                "application/vnd.ms-powerpoint",
                "application/vnd.openxmlformats-officedocument.presentationml.presentation"
            )
        ) {
            selectedFileUri = uri
            tvFileName.text = getFileNameFromUri(uri)
        } else {
            Toast.makeText(this, "Hanya file PDF atau PPT yang didukung.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun handleImageSelection(uri: Uri) {
        selectedImageUri = uri
        previewImage.setImageURI(uri)
    }

    private fun getFileNameFromUri(uri: Uri): String {
        var fileName = "Nama file tidak ditemukan"
        if (uri.scheme == "content") {
            contentResolver.query(uri, arrayOf(OpenableColumns.DISPLAY_NAME), null, null, null)?.use {
                if (it.moveToFirst()) {
                    fileName = it.getString(it.getColumnIndexOrThrow(OpenableColumns.DISPLAY_NAME))
                }
            }
        } else if (uri.scheme == "file") {
            fileName = uri.lastPathSegment ?: fileName
        }
        return fileName
    }

    private fun getRealPathFromURI(uri: Uri): File {
        val fileName = getFileNameFromUri(uri)
        val file = File(cacheDir, fileName)
        contentResolver.openInputStream(uri)?.use { input ->
            file.outputStream().use { output ->
                input.copyTo(output)
            }
        }
        return file
    }

    private fun submitData() {
        val title = etTitle.text.toString()
        val description = etDescription.text.toString()

        if (title.isBlank() || description.isBlank() || (selectedFileUri == null && selectedImageUri == null)) {
            Toast.makeText(this, "Harap isi semua kolom dan pilih file/gambar", Toast.LENGTH_SHORT).show()
            return
        }

        val titleBody = RequestBody.create("text/plain".toMediaTypeOrNull(), title)
        val descriptionBody = RequestBody.create("text/plain".toMediaTypeOrNull(), description)

        var filePart: MultipartBody.Part? = null
        selectedFileUri?.let {
            val file = getRealPathFromURI(it)
            val requestBody = RequestBody.create("application/pdf".toMediaTypeOrNull(), file)
            filePart = MultipartBody.Part.createFormData("file", file.name, requestBody)
        }

        var imagePart: MultipartBody.Part? = null
        selectedImageUri?.let {
            val file = getRealPathFromURI(it)
            val mimeType = contentResolver.getType(it)
            val requestBody = when (mimeType) {
                "image/jpeg" -> RequestBody.create("image/jpeg".toMediaTypeOrNull(), file)
                "image/png" -> RequestBody.create("image/png".toMediaTypeOrNull(), file)
                else -> throw IllegalArgumentException("Tipe file tidak valid")
            }
            imagePart = MultipartBody.Part.createFormData("image", file.name, requestBody)
        }

        viewModel.uploadFile(titleBody, descriptionBody, filePart, imagePart)
    }

    private fun observeViewModel() {
        viewModel.uploadStatus.observe(this) { status ->
            Toast.makeText(this, status.name, Toast.LENGTH_SHORT).show()
            if (status == FileViewModel.UploadStatus.SUCCESS) {
                finish()
            }
        }
    }

    private fun cacheFileFromUri(uri: Uri): File {
        val file = File(cacheDir, "cached_file_${System.currentTimeMillis()}")
        contentResolver.openInputStream(uri)?.use { inputStream ->
            FileOutputStream(file).use { outputStream ->
                inputStream.copyTo(outputStream)
            }
        }
        return file
    }
}
