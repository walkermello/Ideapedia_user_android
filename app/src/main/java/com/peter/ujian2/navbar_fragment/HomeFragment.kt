package com.peter.ujian2.navbar_fragment

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
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

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [HomeFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class HomeFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    private lateinit var viewModel: UserViewModel
    private lateinit var lstUser: RecyclerView
    private lateinit var swipeRefreshLayout: SwipeRefreshLayout
    private lateinit var editTextSearch: EditText
    private lateinit var imgSearch: ImageView
    private lateinit var userPagingAdapter: UserPagingAdapter
    private lateinit var loadStateAdapter: LoadStateAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_home, container, false)

        lstUser = view.findViewById(R.id.lstUser)
        swipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayout)
        editTextSearch = view.findViewById(R.id.editTextSearch)
        imgSearch = view.findViewById(R.id.imgSearch)

        // Set up RecyclerView dan adapter
        userPagingAdapter = UserPagingAdapter(::onEditUser, ::showDeleteDialog)
        loadStateAdapter = LoadStateAdapter { userPagingAdapter.retry() }
        lstUser.layoutManager = LinearLayoutManager(requireContext())
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

        // Fungsi pencarian
        imgSearch.setOnClickListener {
            val query = editTextSearch.text.toString()
            Log.d("HomeFragment", "Query: $query")
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
                    Toast.makeText(requireContext(), "Gagal memuat data", Toast.LENGTH_SHORT).show()
                }
            }
        }

        return view
    }

    // Dialog konfirmasi untuk menghapus user
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
                viewModel.deleteUser(userId) // Panggil deleteUser dari ViewModel
                dialog.dismiss() // Dismiss dialog setelah klik
                refreshData()
            } else {
                Toast.makeText(requireContext(), "ID pengguna tidak valid", Toast.LENGTH_SHORT).show()
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
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment HomeFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            HomeFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}