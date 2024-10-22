package com.peter.ujian2

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.paging.LoadState
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.peter.ujian2.adapter.LoadStateAdapter
import com.peter.ujian2.adapter.UserPagingAdapter
import com.peter.ujian2.model.UserItem
import com.peter.ujian2.viewmodel.UserViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var viewModel: UserViewModel
    private lateinit var lstUser: RecyclerView
    private lateinit var btnAdd: Button
    private lateinit var swipeRefreshLayout: SwipeRefreshLayout
    private lateinit var editTextSearch: EditText
    private lateinit var imgSearch: ImageView
    private lateinit var userPagingAdapter: UserPagingAdapter
    private lateinit var loadStateAdapter: LoadStateAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        btnAdd = findViewById(R.id.btnAdd)
        lstUser = findViewById(R.id.lstUser)
        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout)
        editTextSearch = findViewById(R.id.editTextSearch)
        imgSearch = findViewById(R.id.imgSearch)

        // Set up RecyclerView dan adapter
        userPagingAdapter = UserPagingAdapter(::onEditUser, ::showDeleteDialog)
        loadStateAdapter = LoadStateAdapter { userPagingAdapter.retry() }
        lstUser.layoutManager = LinearLayoutManager(this)
        lstUser.adapter = userPagingAdapter.withLoadStateFooter(loadStateAdapter)

        viewModel = ViewModelProvider(this).get(UserViewModel::class.java)

        // Mengambil data paging
        lifecycleScope.launch(Dispatchers.Main) {
            viewModel.pagingDataFlow.collectLatest { pagingData ->
                userPagingAdapter.submitData(pagingData)
            }
        }

        // Swipe refresh untuk reload data
        swipeRefreshLayout.setOnRefreshListener {
            viewModel.updateSearchQuery(null) // Reset pencarian saat refresh
            userPagingAdapter.refresh() // Muat ulang data di adapter
            swipeRefreshLayout.isRefreshing = false // Hentikan animasi refresh
        }

        // Fungsi untuk menambahkan user baru
        btnAdd.setOnClickListener {
            startActivity(Intent(this, AddUser::class.java))
        }

        // Fungsi pencarian
        imgSearch.setOnClickListener {
            val query = editTextSearch.text.toString()
            Log.d("MainActivity", "Query: $query")
            viewModel.searchUserByName(query) // Update fungsi getUser untuk menerima query
        }

        // Memantau LoadState untuk menampilkan loading spinner
        userPagingAdapter.addLoadStateListener { loadState ->
            when (loadState.source.refresh) {
                is LoadState.Loading -> {
                    swipeRefreshLayout.isRefreshing = true // Animasi refresh dimulai
                }
                is LoadState.NotLoading -> {
                    swipeRefreshLayout.isRefreshing = false // Animasi refresh berhenti
                }
                is LoadState.Error -> {
                    swipeRefreshLayout.isRefreshing = false // Animasi refresh berhenti jika terjadi error
                    Toast.makeText(this, "Gagal memuat data", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    // Dialog konfirmasi untuk menghapus user
    private fun showDeleteDialog(item: UserItem) {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.activity_delete_user, null)
        val btnDelete = dialogView.findViewById<Button>(R.id.btnDelete)
        val btnCancel = dialogView.findViewById<Button>(R.id.btnCancel)

        val dialog = AlertDialog.Builder(this)
            .setView(dialogView)
            .create()

        btnDelete.setOnClickListener {
            val userId = item.id?.toIntOrNull()
            if (userId != null) {
                viewModel.deleteUser(userId) // Panggil deleteUser dari ViewModel
                dialog.dismiss() // Dismiss dialog setelah klik
                refreshData()
            } else {
                Toast.makeText(this, "ID pengguna tidak valid", Toast.LENGTH_SHORT).show()
            }
        }

        btnCancel.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }

    // Tidak diperlukan fungsi ini karena Paging akan mengurus refresh data
    private fun refreshData() {
        userPagingAdapter.refresh() // Jika ingin force refresh
    }

    override fun onResume() {
        super.onResume()
        refreshData() // Lakukan refresh saat kembali ke MainActivity
    }

    // Fungsi untuk mengedit user
    private fun onEditUser(user: UserItem) {
        val intent = Intent(this, EditUser::class.java).apply {
            putExtra("USER_ID", user.id?.toIntOrNull() ?: -1)
            putExtra("USER_NAMA", user.nama)
            putExtra("USER_ALAMAT", user.alamat)
            putExtra("USER_HUTANG", user.hutang.toString())
        }
        startActivity(intent)
    }
}
