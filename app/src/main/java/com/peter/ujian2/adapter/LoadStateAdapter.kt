package com.peter.ujian2.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ProgressBar
import androidx.paging.LoadState
import androidx.paging.LoadStateAdapter
import androidx.recyclerview.widget.RecyclerView
import com.peter.ujian2.R

class LoadStateAdapter(private val retry: () -> Unit) : LoadStateAdapter<LoadStateViewHolder>() {

    override fun onBindViewHolder(holder: LoadStateViewHolder, loadState: LoadState) {
        holder.bind(loadState)
    }

    override fun onCreateViewHolder(parent: ViewGroup, loadState: LoadState): LoadStateViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_load_state, parent, false)
        return LoadStateViewHolder(view, retry)
    }
}

// Kelas LoadStateViewHolder sebagai kelas terpisah
class LoadStateViewHolder(itemView: View, private val retry: () -> Unit) : RecyclerView.ViewHolder(itemView) {
    private val progressBar: ProgressBar = itemView.findViewById(R.id.progressBar)
    private val retryButton: Button = itemView.findViewById(R.id.retryButton)

    fun bind(loadState: LoadState) {
        // Binding data untuk LoadState
        when (loadState) {
            is LoadState.Loading -> {
                progressBar.visibility = View.VISIBLE
                retryButton.visibility = View.GONE
            }
            is LoadState.Error -> {
                progressBar.visibility = View.GONE
                retryButton.visibility = View.VISIBLE
                retryButton.setOnClickListener { retry() }
            }
            is LoadState.NotLoading -> {
                progressBar.visibility = View.GONE
                retryButton.visibility = View.GONE // Menyembunyikan tombol retry saat tidak ada loading atau error
            }
        }
    }
}
