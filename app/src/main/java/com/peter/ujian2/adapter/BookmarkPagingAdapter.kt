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
import com.peter.ujian2.model.DetailIdea
import com.peter.ujian2.model.Idea
import com.peter.ujian2.utils.Constants
import com.squareup.picasso.Picasso
import java.time.format.DateTimeFormatter

class BookmarkPagingAdapter(private val itemClickListener: (Idea) -> Unit) : PagingDataAdapter<Idea, BookmarkPagingAdapter.BookmarkViewHolder>(DIFF_CALLBACK) {

    // Set to keep track of bookmarked items' IDs
    private val bookmarkedItems = mutableSetOf<Long>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BookmarkViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_list, parent, false)
        return BookmarkViewHolder(view)
    }

    override fun onBindViewHolder(holder: BookmarkViewHolder, position: Int) {
        val idea = getItem(position)
        if (idea != null) {
            // Set the initial state to `star_filled` as all items are bookmarked in the bookmark fragment
            bookmarkedItems.add(idea.id ?: -1L)  // Automatically add to the bookmarked set when displaying
            holder.bind(idea, itemClickListener, position)
        }
    }

    // Use the super method for getting item count
    override fun getItemCount(): Int = super.getItemCount()

    inner class BookmarkViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
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

        fun bind(idea: Idea, itemClickListener: (Idea) -> Unit, position: Int) {
            // Handle image URLs
            val userId = idea.user?.id ?: 1 // Fallback to 1 if null
            val imgUser = "${Constants.BASE_URL}user/image/$userId"
            Picasso.get()
                .load(imgUser)
                .placeholder(R.drawable.pdf_img)
                .error(R.drawable.pdf_img)
                .into(imgProfile)

            val imgIdea = "${Constants.BASE_URL}idea/image/${idea.id}"
            Picasso.get()
                .load(imgIdea)
                .placeholder(R.drawable.pdf_img)
                .error(R.drawable.pdf_img)
                .into(postImageView)

            // Handle date formatting and null values
            val createdAt = idea.createdAt?.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
            txtPostDate.text = createdAt ?: "Unknown Date"

            txtNama.text = idea.user?.username ?: "Unknown User"
            txtTitle.text = idea.judul ?: "Untitled" // Ensure title exists
            txtDescription.text = idea.deskripsi.take(100) + "..." // Safely truncate description
            txtMore.text = "more"

            txtMore.setOnClickListener {
                if (!isExpanded) {
                    txtDescription.text = idea.deskripsi // Show full description
                    txtMore.text = "less"
                    isExpanded = true
                } else {
                    txtDescription.text = idea.deskripsi.take(100) + "..." // Truncate description
                    txtMore.text = "more"
                    isExpanded = false
                }
            }

            // Bookmark functionality: All items initially show star_filled
            btnBookmark.setImageResource(R.drawable.star_filled)

            btnBookmark.setOnClickListener {
                if (bookmarkedItems.contains(idea.id)) {
                    // Remove from bookmarked items and change the icon to 'star'
                    bookmarkedItems.remove(idea.id)
                    btnBookmark.setImageResource(R.drawable.star)
                    Toast.makeText(itemView.context, "Removed from Bookmark List", Toast.LENGTH_SHORT).show()
                } else {
                    // Add to bookmarked items and keep the icon 'star_filled'
                    bookmarkedItems.add(idea.id ?: -1L)
                    btnBookmark.setImageResource(R.drawable.star_filled)
                    Toast.makeText(itemView.context, "Added to Bookmark List", Toast.LENGTH_SHORT).show()
                }
                notifyItemChanged(position) // Notify item changed to update the UI
            }

            imgDetail.setOnClickListener {
                itemClickListener(idea) // Directly notify listener with Idea
            }
        }
    }

    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<Idea>() {
            override fun areItemsTheSame(oldItem: Idea, newItem: Idea): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: Idea, newItem: Idea): Boolean {
                return oldItem == newItem
            }
        }
    }
}
