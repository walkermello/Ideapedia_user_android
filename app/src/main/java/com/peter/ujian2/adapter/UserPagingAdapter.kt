package com.peter.ujian2.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.paging.PagingData
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.peter.ujian2.R
import com.peter.ujian2.model.UserItem
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class UserPagingAdapter() : PagingDataAdapter<UserItem, UserPagingAdapter.UserViewHolder>(DIFF_CALLBACK) {

    private val bookmarkedItems = mutableSetOf<Int>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_list, parent, false)
        return UserViewHolder(view)
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        val user = getItem(position)
        user?.let { holder.bind(it) }
    }

    inner class UserViewHolder(itemView: View, ) : RecyclerView.ViewHolder(itemView) {
        private val btnBookmark: ImageView = itemView.findViewById(R.id.btnBookmark)
        private val txtNama: TextView = itemView.findViewById(R.id.txtNama)

        fun bind(user: UserItem) {
            txtNama.text = user.nama

            // Update status bookmark
            btnBookmark.setImageResource(
                if (bookmarkedItems.contains(adapterPosition)) R.drawable.star_filled
                else R.drawable.star
            )

            btnBookmark.setOnClickListener {
                if (bookmarkedItems.contains(adapterPosition)) {
                    bookmarkedItems.remove(adapterPosition)
                } else {
                    bookmarkedItems.add(adapterPosition)
                }
                notifyItemChanged(adapterPosition) // Update hanya item ini
            }
        }
    }

    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<UserItem>() {
            override fun areItemsTheSame(oldItem: UserItem, newItem: UserItem): Boolean {
                return oldItem.id == newItem.id // Bandingkan berdasarkan ID
            }

            override fun areContentsTheSame(oldItem: UserItem, newItem: UserItem): Boolean {
                return oldItem == newItem // Bandingkan seluruh konten
            }
        }
    }
}
