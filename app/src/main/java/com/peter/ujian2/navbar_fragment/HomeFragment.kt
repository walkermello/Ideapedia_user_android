// Import tetap sama, tidak ada perubahan pada bagian import
package com.peter.ujian2.navbar_fragment

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.paging.LoadState
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.peter.ujian2.AddUser
import com.peter.ujian2.EditUser
import com.peter.ujian2.R
import com.peter.ujian2.adapter.LoadStateAdapter
import com.peter.ujian2.adapter.UserPagingAdapter
import com.peter.ujian2.model.UserItem
import com.peter.ujian2.viewmodel.UserViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class HomeFragment : Fragment() {
    private lateinit var viewModel: UserViewModel
    private lateinit var lstUser: RecyclerView
    private lateinit var swipeRefreshLayout: SwipeRefreshLayout
    private lateinit var editTextSearch: EditText
    private lateinit var constraintLayout: ConstraintLayout
    private lateinit var arrowBack: ImageView
    private lateinit var userPagingAdapter: UserPagingAdapter
    private lateinit var loadStateAdapter: LoadStateAdapter

    // Variabel untuk mengatur tata letak ketika search aktif atau tidak
    private val constraintSetExpanded = ConstraintSet()
    private val constraintSetCollapsed = ConstraintSet()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_home, container, false)

        // Inisialisasi view berdasarkan ID layout
        lstUser = view.findViewById(R.id.lstUser)
        swipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayout)
        editTextSearch = view.findViewById(R.id.editTextSearch)
        constraintLayout = view.findViewById(R.id.main)
        arrowBack = view.findViewById(R.id.arrowBack)

        // Atur tata letak awal
        constraintSetExpanded.clone(constraintLayout)
        constraintSetCollapsed.clone(constraintLayout)
        constraintSetCollapsed.setVisibility(R.id.arrowBack, View.VISIBLE)
        constraintSetCollapsed.connect(R.id.editTextSearch, ConstraintSet.START, R.id.arrowBack, ConstraintSet.END, 8)
        constraintSetCollapsed.constrainPercentWidth(R.id.editTextSearch, 0.8f)

        // Atur RecyclerView dan adapter Paging
        userPagingAdapter = UserPagingAdapter(::onEditUser, ::showDeleteDialog)
        loadStateAdapter = LoadStateAdapter { userPagingAdapter.retry() }
        lstUser.layoutManager = LinearLayoutManager(requireContext())
        lstUser.adapter = userPagingAdapter.withLoadStateFooter(loadStateAdapter)

        // Inisialisasi ViewModel
        viewModel = ViewModelProvider(this).get(UserViewModel::class.java)

        // Pengambilan data paging
        lifecycleScope.launch(Dispatchers.IO) {
            viewModel.pagingDataFlow.collectLatest { pagingData ->
                userPagingAdapter.submitData(pagingData)
            }
        }

        // Mengatur Swipe Refresh Layout
        swipeRefreshLayout.setOnRefreshListener {
            editTextSearch.setText("") // Reset search text
            viewModel.updateSearchQuery(null) // Reset query
            userPagingAdapter.refresh() // Muat ulang data
            swipeRefreshLayout.isRefreshing = false
        }

        // Menampilkan arrowBack saat EditText mendapat fokus (untuk melakukan pencarian)
        editTextSearch.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                // Terapkan tata letak ketika EditText mendapat fokus
                constraintSetCollapsed.applyTo(constraintLayout)
            }
        }

        // Mengatur pencarian pada editTextSearch
        editTextSearch.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                val query = editTextSearch.text.toString().trim()
                if (query.isNotEmpty()) {
                    viewModel.searchUserByName(query)
                } else {
                    Toast.makeText(requireContext(), "Masukkan teks untuk pencarian", Toast.LENGTH_SHORT).show()
                }

                // Sembunyikan keyboard setelah pencarian
                val imm = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(editTextSearch.windowToken, 0)

                true
            } else {
                false
            }
        }

        // Atur kembali tata letak ketika arrowBack diklik
        arrowBack.setOnClickListener {
            editTextSearch.clearFocus()
            editTextSearch.setText("") // Menghapus teks pencarian jika diinginkan
            constraintSetExpanded.applyTo(constraintLayout)
        }

        // Memantau LoadState untuk menampilkan loading spinner
        userPagingAdapter.addLoadStateListener { loadState ->
            when (loadState.source.refresh) {
                is LoadState.Loading -> swipeRefreshLayout.isRefreshing = true
                is LoadState.NotLoading -> swipeRefreshLayout.isRefreshing = false
                is LoadState.Error -> {
                    swipeRefreshLayout.isRefreshing = false
                    Toast.makeText(requireContext(), "Gagal memuat data", Toast.LENGTH_SHORT).show()
                }
            }
        }

        return view
    }

    // Menampilkan dialog konfirmasi untuk menghapus user
    private fun showDeleteDialog(item: UserItem) {
        val dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.activity_delete_user, null)
        val btnDelete = dialogView.findViewById<Button>(R.id.btnDelete)
        val btnCancel = dialogView.findViewById<Button>(R.id.btnCancel)

        val dialog = AlertDialog.Builder(requireContext())
            .setView(dialogView)
            .create()

        btnDelete.setOnClickListener {
            val userId = item.id?.toIntOrNull()
            if (userId != null) {
                viewModel.deleteUser(userId) // Panggil fungsi delete di ViewModel
                dialog.dismiss()
                userPagingAdapter.refresh() // Segarkan data setelah menghapus
            } else {
                Toast.makeText(requireContext(), "ID pengguna tidak valid", Toast.LENGTH_SHORT).show()
            }
        }

        btnCancel.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }

    // Fungsi untuk mengedit user
    private fun onEditUser(user: UserItem) {
        val intent = Intent(requireContext(), EditUser::class.java).apply {
            putExtra("USER_ID", user.id?.toIntOrNull() ?: -1)
            putExtra("USER_NAMA", user.nama)
            putExtra("USER_ALAMAT", user.alamat)
            putExtra("USER_HUTANG", user.hutang.toString())
        }
        startActivity(intent)
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            HomeFragment().apply {
                arguments = Bundle().apply {
                    putString("param1", param1)
                    putString("param2", param2)
                }
            }
    }
}
