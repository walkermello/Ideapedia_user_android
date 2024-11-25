package com.peter.ujian2.viewmodel

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.peter.ujian2.model.HistoryItem
import com.peter.ujian2.pagination.HistoryPagingSource
import com.peter.ujian2.services.IdeaServices
import com.peter.ujian2.services.NetworkConfig
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flatMapLatest

class HistoryViewModel(application: Application) : AndroidViewModel(application) {

    private val ideaServices: IdeaServices = NetworkConfig(application).getIdeaServices()

    private val searchQueryFlow = MutableStateFlow<String?>(null)

    // Menampung PagingData Flow untuk history
    private val _pagingDataFlow: Flow<PagingData<HistoryItem>> = searchQueryFlow
        .flatMapLatest { query ->
            // Ambil userId dari SharedPreferences
            val userId = getUserId()

            // Gunakan HistoryPagingSource dengan userId
            Pager(
                config = PagingConfig(pageSize = 5, enablePlaceholders = false),
                pagingSourceFactory = { HistoryPagingSource(ideaServices, userId, getApplication()) }
            ).flow
        }.cachedIn(viewModelScope)

    val pagingDataFlow: Flow<PagingData<HistoryItem>> get() = _pagingDataFlow

    // Fungsi untuk memperbarui query pencarian dan memicu flow
    fun updateSearchQuery(query: String?) {
        searchQueryFlow.value = query
    }

    // Fungsi untuk mendapatkan userId dari SharedPreferences atau session
    private fun getUserId(): Long {
        val sharedPreferences: SharedPreferences = getApplication<Application>().getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
        return sharedPreferences.getLong("user_id", 0L) // Ambil user_id yang tersimpan
    }
}
