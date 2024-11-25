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
import com.peter.ujian2.adapter.FeedsPagingAdapter
import com.peter.ujian2.adapter.LoadStateAdapter
import com.peter.ujian2.model.DetailIdea
import com.peter.ujian2.model.Idea
import com.peter.ujian2.viewmodel.FileViewModel
import com.squareup.picasso.Picasso
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class HomeFragment : Fragment() {
    private lateinit var viewModel: FileViewModel
    private lateinit var lstIdea: RecyclerView
    private lateinit var swipeRefreshLayout: SwipeRefreshLayout
    private lateinit var editTextSearch: EditText
    private lateinit var constraintLayout: ConstraintLayout
    private lateinit var arrowBack: ImageView
    private lateinit var feedsPagingAdapter: FeedsPagingAdapter

    private val constraintSetExpanded = ConstraintSet()
    private val constraintSetCollapsed = ConstraintSet()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_home, container, false)

        viewModel = ViewModelProvider(this).get(FileViewModel::class.java)

        initViews(view)
        setupRecyclerView()
        setupViewModel()
        setupSearchFunctionality()
        setupSwipeRefresh()
        observeLoadState()
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
        // Pass FileViewModel to the adapter
        feedsPagingAdapter = FeedsPagingAdapter(
            itemClickListener = { idea -> showUserDetailsBottomSheet(idea) },
            viewModel = viewModel // Pass FileViewModel here
        )

        lstIdea.layoutManager = LinearLayoutManager(requireContext())
        lstIdea.adapter = feedsPagingAdapter.withLoadStateFooter(LoadStateAdapter { feedsPagingAdapter.retry() })
    }

    private fun setupViewModel() {
        lifecycleScope.launch(Dispatchers.IO) {
            viewModel.pagingDataFlow.collectLatest { pagingData ->
                feedsPagingAdapter.submitData(pagingData)
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
            feedsPagingAdapter.refresh()
            swipeRefreshLayout.isRefreshing = false
        }
    }

    private fun observeLoadState() {
        feedsPagingAdapter.addLoadStateListener { loadState ->
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

    private fun showUserDetailsBottomSheet(detailIdea: DetailIdea) {
        val bottomSheetDialog = BottomSheetDialog(requireContext())
        val dialogView = layoutInflater.inflate(R.layout.fragment_bottom_sheet_dialog, null)

        val userImageView = dialogView.findViewById<ImageView>(R.id.imgPostDetail)
        val txtTitle = dialogView.findViewById<TextView>(R.id.txtPostDetailTitle)
        val userNameTextView = dialogView.findViewById<TextView>(R.id.userNameTextView)
        val txtPenguji1 = dialogView.findViewById<TextView>(R.id.txtPenguji1)
        val txtPenguji2 = dialogView.findViewById<TextView>(R.id.txtPenguji2)
        val txtPenguji3 = dialogView.findViewById<TextView>(R.id.txtPenguji3)
        val txtDescription = dialogView.findViewById<TextView>(R.id.txtPostDetailDescription)
        val txtFeedback = dialogView.findViewById<TextView>(R.id.txtPostDetailFeedback)

        txtTitle.text = detailIdea.idea.judul
// Fetch the user names for penguji1, penguji2, and penguji3
        viewModel.getUserById(detailIdea.idea.pengujiPertama) { userName ->
            txtPenguji1.text = userName
        }

        viewModel.getUserById(detailIdea.idea.pengujiKedua) { userName ->
            txtPenguji2.text = userName
        }

        viewModel.getUserById(detailIdea.idea.pengujiKetiga) { userName ->
            txtPenguji3.text = userName
        }
        userNameTextView.text = detailIdea.idea.user.username
        if (!detailIdea.idea.fileImage.isNullOrEmpty()) {
            Picasso.get()
                .load(detailIdea.idea.fileImage)
                .placeholder(R.drawable.pdf_img)
                .error(R.drawable.pdf_img)
                .into(userImageView)
        } else {
            userImageView.setImageResource(R.drawable.pdf_img)
        }

        txtDescription.text = detailIdea.idea.deskripsi
        txtFeedback.text = detailIdea.idea.feedback ?: "No feedback provided"

        dialogView.findViewById<Button>(R.id.btnDownload).setOnClickListener {
            val fileId = detailIdea.idea.id.toString()
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
