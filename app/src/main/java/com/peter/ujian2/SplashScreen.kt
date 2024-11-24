package com.peter.ujian2

import android.content.Intent
import android.os.Bundle
import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SplashScreen : AppCompatActivity() {

    private val requestPermissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
        if (isGranted) {
            // Izin diberikan, lanjutkan ke Login Activity
            proceedToLogin()
        } else {
            // Izin ditolak, tampilkan pesan atau lakukan penanganan sesuai kebutuhan
            proceedToLogin()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_splash_screen)

        // Mengecek izin saat splash screen muncul
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_IMAGES)
                != PackageManager.PERMISSION_GRANTED) {
                requestPermissionLauncher.launch(Manifest.permission.READ_MEDIA_IMAGES)
            } else {
                proceedToLogin()
            }
        } else {
            proceedToLogin()
        }
    }

    private fun proceedToLogin() {
        // Menggunakan lifecycleScope untuk menunggu sebelum melanjutkan ke Login Activity
        lifecycleScope.launch {
            delay(3000) // Tunggu selama 3 detik
            startActivity(Intent(this@SplashScreen, Login::class.java))
            finish()
        }
    }
}
