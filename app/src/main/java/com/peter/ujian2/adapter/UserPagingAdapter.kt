package com.peter.ujian2.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.peter.ujian2.R
import com.peter.ujian2.model.UserItem

class UserPagingAdapter(private val itemClickListener: (UserItem) -> Unit) : PagingDataAdapter<UserItem, UserPagingAdapter.UserViewHolder>(DIFF_CALLBACK) {

    private val bookmarkedItems = mutableSetOf<Int>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_list, parent, false)
        return UserViewHolder(view)
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        val user = getItem(position)
        user?.let { holder.bind(it, itemClickListener) }
    }

    inner class UserViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val btnBookmark: ImageView = itemView.findViewById(R.id.btnBookmark)
        private val txtTitle: TextView = itemView.findViewById(R.id.txtTitle)
        private val txtNama: TextView = itemView.findViewById(R.id.txtNama)
        private val imgDetail: ImageView = itemView.findViewById(R.id.img_detail)
        private val txtDescription: TextView = itemView.findViewById(R.id.txtDescription)
        private val txtMore: TextView = itemView.findViewById(R.id.txtMore)
        private var isExpanded = false

        // Contoh deskripsi panjang, sesuaikan dengan model Anda
        private val fullDescription = "Lorem Ipsum is simply dummy text of the printing and typesetting industry. Lorem Ipsum has been the industry's standard dummy text ever since the 1500s, when an unknown printer took a galley of type and scrambled it to make a type specimen book. It has survived not only five centuries, but also the leap into electronic typesetting, remaining essentially unchanged. It was popularised in the 1960s with the release of Letraset sheets containing Lorem Ipsum passages, and more recently with desktop publishing software like Aldus PageMaker including versions of Lorem Ipsum."
        private val fullTitle = "Lorem Ipsum is simply dummy text of the printing and typesetting industry. Lorem Ipsum has been the industry's standard dummy text ever since the 1500s, when an unknown printer took a galley of type and scrambled it to make a type specimen book. It has survived not only five centuries, but also the leap into electronic typesetting, remaining essentially unchanged. It was popularised in the 1960s with the release of Letraset sheets containing Lorem Ipsum passages, and more recently with desktop publishing software like Aldus PageMaker including versions of Lorem Ipsum."

        fun bind(user: UserItem, itemClickListener: (UserItem) -> Unit) {
            txtNama.text = user.nama
            txtTitle.text = fullTitle
            txtDescription.text = fullDescription.substring(0, 100) + "..." // Set deskripsi awal
            txtMore.text = "more" // Set teks "more" awal

            // Mengatur click listener untuk txtMore
            txtMore.setOnClickListener {
                if (!isExpanded) {
                    txtDescription.text = fullDescription // Tampilkan deskripsi penuh
                    txtMore.text = "less" // Ubah "more" menjadi "less"
                    isExpanded = true
                } else {
                    txtDescription.text = fullDescription.substring(0, 100) + "..." // Truncate deskripsi lagi
                    txtMore.text = "more" // Kembalikan ke "more"
                    isExpanded = false
                }
            }

            // Update status bookmark
            btnBookmark.setImageResource(
                if (bookmarkedItems.contains(adapterPosition)) R.drawable.star_filled
                else R.drawable.star
            )

            btnBookmark.setOnClickListener {
                // Hanya mengubah status bookmark tanpa mempengaruhi teks deskripsi
                if (bookmarkedItems.contains(adapterPosition)) {
                    bookmarkedItems.remove(adapterPosition)
                    Toast.makeText(itemView.context, "Removed from Bookmark List", Toast.LENGTH_SHORT).show()
                } else {
                    bookmarkedItems.add(adapterPosition)
                    Toast.makeText(itemView.context, "Added to Bookmark List", Toast.LENGTH_SHORT).show()
                }
                notifyItemChanged(adapterPosition) // Update hanya item ini
            }

            // Set up click listener untuk imgDetail
            imgDetail.setOnClickListener {
                itemClickListener(user) // Beri tahu listener secara langsung
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

