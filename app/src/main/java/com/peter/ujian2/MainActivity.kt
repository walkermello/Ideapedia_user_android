package com.peter.ujian2

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.peter.ujian2.navbar_fragment.BookmarkFragment
import com.peter.ujian2.navbar_fragment.HistoryFragment
import com.peter.ujian2.navbar_fragment.HomeFragment
import com.peter.ujian2.navbar_fragment.ProfileFragment

class MainActivity : AppCompatActivity() {

    private lateinit var bottomNavigationView: BottomNavigationView
    private lateinit var btnAdd: FloatingActionButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main) // Menggunakan layout activity_main.xml

        // Inisialisasi BottomNavigationView
        bottomNavigationView = findViewById(R.id.bottomNavigation)
        btnAdd = findViewById(R.id.addFabBtn)

        // Memuat fragment HomeFragment sebagai default
        if (savedInstanceState == null) {
            loadFragment(HomeFragment())
        }

        // Menangani item click di BottomNavigationView
        bottomNavigationView.setOnNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.bottom_home -> {
                    loadFragment(HomeFragment())
                    true
                }
                R.id.bottom_bookmark -> {
                    loadFragment(BookmarkFragment())
                    true
                }
                R.id.bottom_history -> {
                    loadFragment(HistoryFragment())
                    true
                }
                R.id.bottom_profile -> {
                    loadFragment(ProfileFragment())
                    true
                }
                else -> false
            }
        }

        btnAdd.setOnClickListener {
            val intent = Intent(this, AddUser::class.java) // Ganti AddDataActivity dengan nama activity yang sesuai
            startActivity(intent)
        }
    }

    // Fungsi untuk memuat fragment
    private fun loadFragment(fragment: Fragment) {
        // Ganti fragment saat sudah ada
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.fragmentContainer, fragment)
        transaction.addToBackStack(null) // Tambahkan fragment ke back stack
        transaction.commit()
    }
}
