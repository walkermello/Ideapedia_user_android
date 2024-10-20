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
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.peter.ujian2.adapter.ItemAdapter
import com.peter.ujian2.model.User
import com.peter.ujian2.model.UserItem
import com.peter.ujian2.viewmodel.UserViewModel

class MainActivity : AppCompatActivity() {

    private lateinit var viewModel: UserViewModel
    private lateinit var lstUser: RecyclerView
    private lateinit var btnAdd: Button
    private lateinit var itemAdapter: ItemAdapter
    private lateinit var swipeRefreshLayout: SwipeRefreshLayout
    private lateinit var editTextSearch: EditText
    private lateinit var imgSearch: ImageView

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
        itemAdapter = ItemAdapter(mutableListOf(), ::onEditUser, ::showDeleteDialog)
        lstUser.layoutManager = LinearLayoutManager(this)
        lstUser.adapter = itemAdapter

        viewModel = ViewModelProvider(this).get(UserViewModel::class.java)

        viewModel.getUser.observe(this) { response ->
            val users = response.data?.newUser?.filterNotNull() ?: emptyList()
            itemAdapter.updateData(users)
            swipeRefreshLayout.isRefreshing = false
        }

        btnAdd.setOnClickListener {
            startActivity(Intent(this, AddUser::class.java))
        }

        swipeRefreshLayout.setOnRefreshListener {
            viewModel.getUser()
        }

        imgSearch.setOnClickListener {
            val query = editTextSearch.text.toString()
            itemAdapter.filter(query)
        }
    }

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
                viewModel.deleteUser(userId) // Panggil deleteUser
                viewModel.deleteUserSuccess.observe(this) { success ->
                    if (success) {
                        itemAdapter.removeItem(item) // Hapus item dari adapter
                        viewModel.getUser()
                    } else {
                        Toast.makeText(this, "Gagal menghapus data pengguna", Toast.LENGTH_SHORT).show()
                    }
                }
                dialog.dismiss()
            } else {
                Toast.makeText(this, "ID pengguna tidak valid", Toast.LENGTH_SHORT).show()
            }
        }

        btnCancel.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }

    override fun onResume() {
        super.onResume()
        viewModel.getUser()
    }

    private fun onEditUser(user: UserItem) {
        val intent = Intent(this, EditUser::class.java)
        intent.putExtra("USER_ID", user.id?.toIntOrNull() ?: -1) // Kirim ID pengguna sebagai Int
        intent.putExtra("USER_NAMA", user.nama)
        intent.putExtra("USER_ALAMAT", user.alamat)
        intent.putExtra("USER_HUTANG", user.hutang)
        startActivity(intent)
    }

}
