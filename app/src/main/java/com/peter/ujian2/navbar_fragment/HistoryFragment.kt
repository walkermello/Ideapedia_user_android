package com.peter.ujian2.navbar_fragment

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.paging.LoadState
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.peter.ujian2.R
import com.peter.ujian2.adapter.HistoryPagingAdapter
import com.peter.ujian2.adapter.LoadStateAdapter
import com.peter.ujian2.viewmodel.HistoryViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class HistoryFragment : Fragment() {

    private lateinit var viewModel: HistoryViewModel
    private lateinit var lstHistory: RecyclerView
    private lateinit var swipeRefreshLayout: androidx.swiperefreshlayout.widget.SwipeRefreshLayout
    private lateinit var editTextSearch: EditText
    private lateinit var constraintLayout: ConstraintLayout
    private lateinit var arrowBack: ImageView
    private lateinit var historyAdapter: HistoryPagingAdapter

    private val constraintSetExpanded = ConstraintSet()
    private val constraintSetCollapsed = ConstraintSet()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_history, container, false)

        // Initialize ViewModel
        viewModel = ViewModelProvider(this).get(HistoryViewModel::class.java)

        // Setup RecyclerView
        lstHistory = view.findViewById(R.id.lstHistory)
        lstHistory.layoutManager = LinearLayoutManager(context)

        // Initialize Adapter
        historyAdapter = HistoryPagingAdapter()  // Use your custom adapter
        lstHistory.adapter = historyAdapter.withLoadStateFooter(
            LoadStateAdapter { historyAdapter.retry() }
        )

        // Observe Paging Data Flow
        lifecycleScope.launch {
            viewModel.pagingDataFlow.collectLatest { pagingData ->
                historyAdapter.submitData(pagingData)
            }
        }

        // Observe LoadState for the adapter
        historyAdapter.addLoadStateListener { loadState ->
            // Show/Hide loading spinner or display error messages based on loadState
            // For example, show a loading spinner while loading
            // You can show a toast on error, etc.
            if (loadState.source.refresh is LoadState.Loading) {
                swipeRefreshLayout.isRefreshing = true
            } else {
                swipeRefreshLayout.isRefreshing = false
            }

            if (loadState.source.refresh is LoadState.Error) {
                Toast.makeText(requireContext(), "Gagal memuat data", Toast.LENGTH_SHORT).show()
            }
        }

        initViews(view)
        setupSearchFunctionality()
        setupSwipeRefresh()

        return view
    }

    private fun initViews(view: View) {
        // Initialize other views
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
            historyAdapter.refresh()
        }
    }

    private fun hideKeyboard() {
        val imm = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(editTextSearch.windowToken, 0)
    }
}
