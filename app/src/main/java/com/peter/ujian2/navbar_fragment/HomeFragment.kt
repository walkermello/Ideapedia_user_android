package com.peter.ujian2.navbar_fragment

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
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
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.peter.ujian2.R
import com.peter.ujian2.adapter.LoadStateAdapter
import com.peter.ujian2.adapter.UserPagingAdapter
import com.peter.ujian2.model.UserItem
import com.peter.ujian2.viewmodel.FileViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class HomeFragment : Fragment() {
    private lateinit var viewModel: FileViewModel
    private lateinit var lstUser: RecyclerView
    private lateinit var swipeRefreshLayout: SwipeRefreshLayout
    private lateinit var editTextSearch: EditText
    private lateinit var constraintLayout: ConstraintLayout
    private lateinit var arrowBack: ImageView
    private lateinit var userPagingAdapter: UserPagingAdapter

    private val constraintSetExpanded = ConstraintSet()
    private val constraintSetCollapsed = ConstraintSet()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_home, container, false)
        initViews(view)
        setupRecyclerView()
        setupViewModel()
        setupSearchFunctionality()
        setupSwipeRefresh()
        observeLoadState()
        return view
    }

    private fun initViews(view: View) {
        lstUser = view.findViewById(R.id.lstUser)
        swipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayout)
        editTextSearch = view.findViewById(R.id.editTextSearch)
        constraintLayout = view.findViewById(R.id.main)
        arrowBack = view.findViewById(R.id.arrowBack)

        constraintSetExpanded.clone(constraintLayout)
        constraintSetCollapsed.clone(constraintLayout)
        constraintSetCollapsed.setVisibility(R.id.arrowBack, View.VISIBLE)
        constraintSetCollapsed.connect(R.id.editTextSearch, ConstraintSet.START, R.id.arrowBack, ConstraintSet.END, 8)
        constraintSetCollapsed.constrainPercentWidth(R.id.editTextSearch, 0.8f)
    }

    private fun setupRecyclerView() {
        userPagingAdapter = UserPagingAdapter { userItem -> showUserDetailsBottomSheet(userItem) }
        lstUser.layoutManager = LinearLayoutManager(requireContext())
        lstUser.adapter = userPagingAdapter.withLoadStateFooter(LoadStateAdapter { userPagingAdapter.retry() })
    }

    private fun setupViewModel() {
        viewModel = ViewModelProvider(this).get(FileViewModel::class.java)
        lifecycleScope.launch(Dispatchers.IO) {
            viewModel.pagingDataFlow.collectLatest { pagingData ->
                userPagingAdapter.submitData(pagingData)
            }
        }
    }

    private fun setupSearchFunctionality() {
        editTextSearch.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) constraintSetCollapsed.applyTo(constraintLayout)
        }

        editTextSearch.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                val query = editTextSearch.text.toString().trim()
                if (query.isNotEmpty()) {
                    viewModel.searchUserByName(query)
                } else {
                    Toast.makeText(requireContext(), "Masukkan teks untuk pencarian", Toast.LENGTH_SHORT).show()
                }
                hideKeyboard()
                true
            } else false
        }

        arrowBack.setOnClickListener {
            editTextSearch.clearFocus()
            editTextSearch.setText("")
            constraintSetExpanded.applyTo(constraintLayout)
        }
    }

    private fun setupSwipeRefresh() {
        swipeRefreshLayout.setOnRefreshListener {
            editTextSearch.setText("")
            viewModel.updateSearchQuery(null)
            userPagingAdapter.refresh()
            swipeRefreshLayout.isRefreshing = false
        }
    }

    private fun observeLoadState() {
        userPagingAdapter.addLoadStateListener { loadState ->
            swipeRefreshLayout.isRefreshing = loadState.source.refresh is LoadState.Loading
            if (loadState.source.refresh is LoadState.Error) {
                Toast.makeText(requireContext(), "Gagal memuat data", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun hideKeyboard() {
        val imm = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(editTextSearch.windowToken, 0)
    }

    private fun showDeleteDialog(item: UserItem) {
        val dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.activity_delete_user, null)
        val btnDelete = dialogView.findViewById<Button>(R.id.btnDelete)
        val btnCancel = dialogView.findViewById<Button>(R.id.btnCancel)

        val dialog = AlertDialog.Builder(requireContext())
            .setView(dialogView)
            .create()

        btnDelete.setOnClickListener {
            item.id?.toIntOrNull()?.let { userId ->
                viewModel.deleteUser(userId)
                dialog.dismiss()
                userPagingAdapter.refresh()
            } ?: Toast.makeText(requireContext(), "ID pengguna tidak valid", Toast.LENGTH_SHORT).show()
        }

        btnCancel.setOnClickListener { dialog.dismiss() }

        dialog.show()
    }

    private fun showUserDetailsBottomSheet(userItem: UserItem) {
        val bottomSheetDialog = BottomSheetDialog(requireContext())
        val dialogView = layoutInflater.inflate(R.layout.fragment_bottom_sheet_dialog, null)

        val userNameTextView = dialogView.findViewById<TextView>(R.id.userNameTextView)
        val userImageView = dialogView.findViewById<ImageView>(R.id.imgPostDetail)
        val txtDescription = dialogView.findViewById<TextView>(R.id.txtPostDetailDescription)
        val txtFeedback = dialogView.findViewById<TextView>(R.id.txtPostDetailFeedback)

        userNameTextView.text = userItem.nama
        txtDescription.text = "Lorem Ipsum is simply dummy text of the printing and typesetting industry. Lorem Ipsum has been the industry's standard dummy text ever since the 1500s, when an unknown printer took a galley of type and scrambled it to make a type specimen book. It has survived not only five centuries, but also the leap into electronic typesetting, remaining essentially unchanged. It was popularised in the 1960s with the release of Letraset sheets containing Lorem Ipsum passages, and more recently with desktop publishing software like Aldus PageMaker including versions of Lorem Ipsum."
        txtFeedback.text = "Lorem Ipsum is simply dummy text of the printing and typesetting industry. Lorem Ipsum has been the industry's standard dummy text ever since the 1500s, when an unknown printer took a galley of type and scrambled it to make a type specimen book. It has survived not only five centuries, but also the leap into electronic typesetting, remaining essentially unchanged. It was popularised in the 1960s with the release of Letraset sheets containing Lorem Ipsum passages, and more recently with desktop publishing software like Aldus PageMaker including versions of Lorem Ipsum."

        dialogView.findViewById<Button>(R.id.closeButton).setOnClickListener {
            bottomSheetDialog.dismiss()
        }

        bottomSheetDialog.setContentView(dialogView)
        bottomSheetDialog.show()
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) = HomeFragment().apply {
            arguments = Bundle().apply {
                putString("param1", param1)
                putString("param2", param2)
            }
        }
    }
}
