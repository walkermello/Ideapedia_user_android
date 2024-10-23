package com.peter.ujian2

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SplashScreen : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_splash_screen)
        // Menggunakan lifecycleScope untuk menunggu sebelum melanjutkan ke Login Activity
        lifecycleScope.launch {
            delay(3000) // Tunggu selama 3 detik
            startActivity(Intent(this@SplashScreen, Login::class.java))
            finish()
        }
    }
}