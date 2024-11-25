package com.peter.ujian2.navbar_fragment

import android.content.Context
import android.os.Bundle
import android.util.Log
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
import com.peter.ujian2.adapter.BookmarkPagingAdapter

import com.peter.ujian2.adapter.LoadStateAdapter
import com.peter.ujian2.model.Bookmark
import com.peter.ujian2.model.DetailIdea
import com.peter.ujian2.model.Idea
import com.peter.ujian2.viewmodel.BookmarkViewModel
import com.peter.ujian2.viewmodel.FileViewModel
import com.squareup.picasso.Picasso
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class BookmarkFragment : Fragment() {
    private lateinit var viewModel: BookmarkViewModel
    private lateinit var lstIdea: RecyclerView
    private lateinit var swipeRefreshLayout: SwipeRefreshLayout
    private lateinit var editTextSearch: EditText
    private lateinit var constraintLayout: ConstraintLayout
    private lateinit var arrowBack: ImageView
    private lateinit var bookmarkPagingAdapter: BookmarkPagingAdapter

    private val constraintSetExpanded = ConstraintSet()
    private val constraintSetCollapsed = ConstraintSet()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_bookmark, container, false)

        // Initialize ViewModel
        viewModel = ViewModelProvider(this).get(BookmarkViewModel::class.java)

        // Setup RecyclerView
        lstIdea = view.findViewById(R.id.lstIdea)
        lstIdea.layoutManager = LinearLayoutManager(requireContext())

        // Setup Adapter
        bookmarkPagingAdapter = BookmarkPagingAdapter { idea -> showUserDetailsBottomSheet(idea) }
        lstIdea.adapter = bookmarkPagingAdapter.withLoadStateFooter(LoadStateAdapter { bookmarkPagingAdapter.retry() })

        // Observe Paging Data Flow
        lifecycleScope.launch {
            viewModel.pagingDataFlow.collectLatest { pagingData ->
                bookmarkPagingAdapter.submitData(pagingData)
            }
        }

        // Swipe to refresh
        swipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayout)
        swipeRefreshLayout.setOnRefreshListener {
            bookmarkPagingAdapter.refresh()
            swipeRefreshLayout.isRefreshing = false
        }

        return view
    }

    private fun initViews(view: View) {
        lstIdea = view.findViewById(R.id.lstIdea)
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
        bookmarkPagingAdapter = BookmarkPagingAdapter { idea -> showUserDetailsBottomSheet(idea) }
        lstIdea.layoutManager = LinearLayoutManager(requireContext())
        lstIdea.adapter = bookmarkPagingAdapter.withLoadStateFooter(LoadStateAdapter { bookmarkPagingAdapter.retry() })
    }

    private fun setupViewModel() {
        lifecycleScope.launch(Dispatchers.IO) {
            viewModel.pagingDataFlow.collectLatest { pagingData ->
                bookmarkPagingAdapter.submitData(pagingData)
            }
        }
    }

    private fun setupSearchFunctionality() {
        editTextSearch.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) constraintSetCollapsed.applyTo(constraintLayout)
            else constraintSetExpanded.applyTo(constraintLayout)
        }

        editTextSearch.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                val query = editTextSearch.text.toString().trim()
                if (query.isNotEmpty()) {
                    viewModel.updateSearchQuery(query)
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
            bookmarkPagingAdapter.refresh()
            swipeRefreshLayout.isRefreshing = false
        }
    }

    private fun observeLoadState() {
        bookmarkPagingAdapter.addLoadStateListener { loadState ->
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

    private fun showUserDetailsBottomSheet(idea: Idea) {
        val bottomSheetDialog = BottomSheetDialog(requireContext())
        val dialogView = layoutInflater.inflate(R.layout.fragment_bottom_sheet_dialog, null)

        val userNameTextView = dialogView.findViewById<TextView>(R.id.userNameTextView)
        val userImageView = dialogView.findViewById<ImageView>(R.id.imgPostDetail)
        val txtDescription = dialogView.findViewById<TextView>(R.id.txtPostDetailDescription)
        val txtFeedback = dialogView.findViewById<TextView>(R.id.txtPostDetailFeedback)

        userNameTextView.text = idea.user.username
        if (!idea.fileImage.isNullOrEmpty()) {
            Picasso.get()
                .load(idea.fileImage)
                .placeholder(R.drawable.pdf_img)
                .error(R.drawable.pdf_img)
                .into(userImageView)
        } else {
            userImageView.setImageResource(R.drawable.pdf_img)
        }

        txtDescription.text = idea.deskripsi
        txtFeedback.text = idea.feedback ?: "No feedback provided"

        dialogView.findViewById<Button>(R.id.btnDownload).setOnClickListener {
            val fileId = idea.id.toString()
            viewModel.downloadFile(fileId) { success, message ->
                if (success) {
                    Log.d("Download", "Download berhasil, file disimpan di: $message")
                    Toast.makeText(requireContext(), "Download berhasil: $message", Toast.LENGTH_SHORT).show()
                } else {
                    Log.e("Download", "Download gagal: $message")
                    Toast.makeText(requireContext(), "Download gagal: $message", Toast.LENGTH_SHORT).show()
                }
            }
        }

        bottomSheetDialog.setContentView(dialogView)
        bottomSheetDialog.show()
    }
}
