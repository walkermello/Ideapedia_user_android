package com.peter.ujian2

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.peter.ujian2.viewmodel.FileViewModel
import com.squareup.picasso.Picasso
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File

class AddFiles : AppCompatActivity() {
    private lateinit var etTitle: TextInputEditText
    private lateinit var etDescription: TextInputEditText
    private lateinit var tvFileName: TextView
    private lateinit var previewImage: ImageView
    private lateinit var btnChooseFile: MaterialButton
    private lateinit var btnSubmit: MaterialButton
    private var selectedFileUri: Uri? = null
    private var selectedImageUri: Uri? = null

    private val viewModel: FileViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
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

        // Menambahkan click listener pada btnChooseFile hanya untuk memilih file
        btnChooseFile.setOnClickListener { chooseFile() }

        // Menambahkan click listener pada previewImage untuk memilih gambar
        previewImage.setOnClickListener { chooseImage() }

        btnSubmit.setOnClickListener { submitData() }
    }

    private fun chooseFile() {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "*/*"
        val mimeTypes = arrayOf("application/pdf", "application/vnd.ms-powerpoint")
        intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes)
        startActivityForResult(Intent.createChooser(intent, "Pilih File"), 100)
    }

    private fun chooseImage() {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "image/*"
        startActivityForResult(Intent.createChooser(intent, "Pilih Gambar"), 101)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            val uri = data?.data
            uri?.let {
                if (requestCode == 100) {
                    // File dipilih
                    selectedFileUri = it
                    tvFileName.text = "File Dipilih"
                } else if (requestCode == 101) {
                    // Gambar dipilih
                    selectedImageUri = it
                    Picasso.get().load(it).into(previewImage)
                    tvFileName.text = "Gambar Dipilih"
                }
            }
        }
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
            val file = File(getRealPathFromURI(it))
            val requestBody = RequestBody.create("application/pdf".toMediaTypeOrNull(), file)
            filePart = MultipartBody.Part.createFormData("file", file.name, requestBody)
        }

        var imagePart: MultipartBody.Part? = null
        selectedImageUri?.let {
            val file = File(getRealPathFromURI(it))
            val requestBody = RequestBody.create("image/jpeg".toMediaTypeOrNull(), file)
            imagePart = MultipartBody.Part.createFormData("image", file.name, requestBody)
        }

        viewModel.uploadFile(titleBody, descriptionBody, filePart, imagePart)
    }

    private fun observeViewModel() {
        viewModel.uploadStatus.observe(this) { status ->
            // Menampilkan status sebagai String
            Toast.makeText(this, status.name, Toast.LENGTH_SHORT).show()
        }
    }

    private fun getRealPathFromURI(uri: Uri): String {
        val file = File(cacheDir, "tempfile")
        contentResolver.openInputStream(uri)?.use { input ->
            file.outputStream().use { output ->
                input.copyTo(output)
            }
        }
        return file.absolutePath
    }
}
