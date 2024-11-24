package com.peter.ujian2.adapter

import android.util.Log
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
import com.peter.ujian2.model.DetailIdea
import com.peter.ujian2.utils.Constants
import com.squareup.picasso.Picasso
import java.time.format.DateTimeFormatter

class FeedsPagingAdapter(private val itemClickListener: (DetailIdea) -> Unit) : PagingDataAdapter<DetailIdea, FeedsPagingAdapter.IdeaViewHolder>(DIFF_CALLBACK) {
    private val bookmarkedItems = mutableSetOf<Int>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): IdeaViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_list, parent, false)
        return IdeaViewHolder(view)
    }

    override fun onBindViewHolder(holder: IdeaViewHolder, position: Int) {
        val idea = getItem(position)
        if (idea != null) {
            holder.bind(idea, itemClickListener)
        } else {
            Log.d("FeedsPagingAdapter", "Item at position $position is null")
        }

        idea?.let { holder.bind(it, itemClickListener) }
    }

    inner class IdeaViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val btnBookmark: ImageView = itemView.findViewById(R.id.btnBookmark)
        private val txtTitle: TextView = itemView.findViewById(R.id.txtTitle)
        private val postImageView: ImageView = itemView.findViewById(R.id.postImageView)
        private val imgProfile: ImageView = itemView.findViewById(R.id.imgProfile)
        private val txtNama: TextView = itemView.findViewById(R.id.txtNama)
        private val txtPostDate: TextView = itemView.findViewById(R.id.txtPostDate)
        private val imgDetail: ImageView = itemView.findViewById(R.id.img_detail)
        private val txtDescription: TextView = itemView.findViewById(R.id.txtDescription)
        private val txtMore: TextView = itemView.findViewById(R.id.txtMore)
        private var isExpanded = false



        fun bind(detailIdea: DetailIdea, itemClickListener: (DetailIdea) -> Unit) {

            // Membuat URL dinamis berdasarkan id user
            val userId = detailIdea.idea.user.id ?: 1 // Jika id user tidak ada, fallback ke id default (misalnya 1)
            val imgUser = "${Constants.BASE_URL}user/image/$userId" // URL dinamis untuk gambar berdasarkan id pengguna
            // Memuat gambar dengan Picasso
            Picasso.get()
                .load(imgUser) // Memuat gambar dari URL dinamis
                .placeholder(R.drawable.pdf_img) // Gambar placeholder jika gambar belum dimuat
                .error(R.drawable.pdf_img) // Gambar error jika gagal memuat
                .into(imgProfile) // Memasukkan gambar ke ImageView

            // Membuat URL dinamis berdasarkan id user
            val ideaId = detailIdea.idea.id ?: 1 // Jika id user tidak ada, fallback ke id default (misalnya 1)
            val imgIdea = "${Constants.BASE_URL}idea/image/$ideaId" // URL dinamis untuk gambar berdasarkan id pengguna
            // Memuat gambar dengan Picasso
            Picasso.get()
                .load(imgIdea) // Memuat gambar dari URL dinamis
                .placeholder(R.drawable.pdf_img) // Gambar placeholder jika gambar belum dimuat
                .error(R.drawable.pdf_img) // Gambar error jika gagal memuat
                .into(postImageView) // Memasukkan gambar ke ImageView

            // Mengonversi LocalDateTime ke String
            val createdAt = detailIdea.idea.createdAt?.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
            txtPostDate.text = createdAt ?: "Unknown Date"
            txtNama.text = detailIdea.idea.user?.username ?: "Unknown User" // Handling null safety for username
            txtTitle.text = detailIdea.idea.judul // Ensure this field exists in your model
            txtDescription.text = detailIdea.idea.deskripsi.take(100) + "..." // Safely truncate description
            txtMore.text = "more"

            txtMore.setOnClickListener {
                if (!isExpanded) {
                    txtDescription.text = detailIdea.idea.deskripsi // Show full description
                    txtMore.text = "less"
                    isExpanded = true
                } else {
                    txtDescription.text = detailIdea.idea.deskripsi.take(100) + "..." // Truncate description
                    txtMore.text = "more"
                    isExpanded = false
                }
            }

            // Update bookmark icon based on whether the item is bookmarked
            btnBookmark.setImageResource(
                if (bookmarkedItems.contains(adapterPosition)) R.drawable.star_filled
                else R.drawable.star
            )

            btnBookmark.setOnClickListener {
                if (bookmarkedItems.contains(adapterPosition)) {
                    bookmarkedItems.remove(adapterPosition)
                    Toast.makeText(itemView.context, "Removed from Bookmark List", Toast.LENGTH_SHORT).show()
                } else {
                    bookmarkedItems.add(adapterPosition)
                    Toast.makeText(itemView.context, "Added to Bookmark List", Toast.LENGTH_SHORT).show()
                }
                notifyItemChanged(adapterPosition) // Notify only the changed item
            }

            imgDetail.setOnClickListener {
                itemClickListener(detailIdea) // Directly notify listener with DetailIdea
            }
        }
    }

    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<DetailIdea>() {
            override fun areItemsTheSame(oldItem: DetailIdea, newItem: DetailIdea): Boolean {
                return oldItem.id == newItem.id // Compare based on ID
            }

            override fun areContentsTheSame(oldItem: DetailIdea, newItem: DetailIdea): Boolean {
                return oldItem == newItem // Compare entire content
            }
        }
    }
}
