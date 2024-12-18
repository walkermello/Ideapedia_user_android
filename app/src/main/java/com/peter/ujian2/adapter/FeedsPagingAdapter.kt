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
import com.peter.ujian2.viewmodel.BookmarkViewModel
import com.peter.ujian2.viewmodel.FileViewModel
import com.squareup.picasso.Picasso
import java.time.format.DateTimeFormatter

class FeedsPagingAdapter(
    private val itemClickListener: (DetailIdea) -> Unit,
    private val viewModel: FileViewModel // Use FileViewModel here
) : PagingDataAdapter<DetailIdea, FeedsPagingAdapter.IdeaViewHolder>(DIFF_CALLBACK) {

    private val bookmarkedItems = mutableSetOf<Long>()

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
            // Handle image URLs
            val userId = detailIdea.idea.user?.id ?: 1 // Fallback to 1 if null
            val imgUser = "${Constants.BASE_URL}user/image/$userId"
            Picasso.get()
                .load(imgUser)
                .placeholder(R.drawable.pdf_img)
                .error(R.drawable.pdf_img)
                .into(imgProfile)

            val ideaId = detailIdea.idea.id
            val imgIdea = "${Constants.BASE_URL}idea/image/$ideaId"
            Picasso.get()
                .load(imgIdea)
                .placeholder(R.drawable.pdf_img)
                .error(R.drawable.pdf_img)
                .into(postImageView)

            // Handle date formatting and null values
            val createdAt = detailIdea.idea.createdAt?.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
            txtPostDate.text = createdAt ?: "Unknown Date"

            txtNama.text = detailIdea.idea.user?.username ?: "Unknown User"
            txtTitle.text = detailIdea.idea.judul ?: "Untitled" // Ensure title exists
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

            // Bookmark functionality
            btnBookmark.setImageResource(
                if (bookmarkedItems.contains(detailIdea.idea.id)) R.drawable.star_filled
                else R.drawable.star
            )

            btnBookmark.setOnClickListener {
                if (bookmarkedItems.contains(detailIdea.idea.id)) {
                    bookmarkedItems.remove(detailIdea.idea.id)
                    Toast.makeText(itemView.context, "Removed from Bookmark List", Toast.LENGTH_SHORT).show()
                } else {
                    bookmarkedItems.add(detailIdea.idea.id ?: -1L)
                    Toast.makeText(itemView.context, "Added to Bookmark List", Toast.LENGTH_SHORT).show()
                    // Call the API to add bookmark using FileViewModel
                    viewModel.addBookmark(detailIdea.idea.id ?: -1L)
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
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: DetailIdea, newItem: DetailIdea): Boolean {
                return oldItem == newItem
            }
        }
    }
}