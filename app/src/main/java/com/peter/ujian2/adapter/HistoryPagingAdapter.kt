package com.peter.ujian2.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.peter.ujian2.R
import com.peter.ujian2.model.HistoryItem
import java.time.format.DateTimeFormatter

class HistoryPagingAdapter : PagingDataAdapter<HistoryItem, HistoryPagingAdapter.HistoryViewHolder>(DIFF_CALLBACK) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistoryViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_history, parent, false)
        return HistoryViewHolder(view)
    }

    override fun onBindViewHolder(holder: HistoryViewHolder, position: Int) {
        val historyItem = getItem(position)
        if (historyItem != null) {
            holder.bind(historyItem)
        }
    }

    override fun getItemCount(): Int = super.getItemCount()

    class HistoryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvDate: TextView = itemView.findViewById(R.id.tvDate)
        private val tvMonth: TextView = itemView.findViewById(R.id.tvMonth)
        private val tvYear: TextView = itemView.findViewById(R.id.tvYear)
        private val tvTitle: TextView = itemView.findViewById(R.id.tvTitle)
        private val tvProfile: TextView = itemView.findViewById(R.id.tvProfile)
        private val tvAction: TextView = itemView.findViewById(R.id.tvAction)
        private val tvStatus: TextView = itemView.findViewById(R.id.tvStatus)

        fun bind(item: HistoryItem) {
            // Split the date into day, month, and year
// Ensure that date is not null before trying to format it
            item.date?.let {
                // Format the date into a string (example: "2024-10-01")
                val formattedDate = it.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))

                // Split the formatted date into day, month, and year
                val dateParts = formattedDate.split("-")
                if (dateParts.size == 3) {
                    tvDate.text = dateParts[2] // Day
                    tvMonth.text = dateParts[1] // Month
                    tvYear.text = dateParts[0] // Year
                }
            }
            tvTitle.text = item.title
            tvProfile.text = item.profile
            tvStatus.text = item.status

            // Set background color for action type
            when (item.action) {
                "Upload" -> {
                    tvAction.text = "Upload"
                    tvAction.setBackgroundColor(itemView.context.getColor(android.R.color.holo_green_light))
                }
                "Delete" -> {
                    tvAction.text = "Delete"
                    tvAction.setBackgroundColor(itemView.context.getColor(android.R.color.holo_red_light))
                }
                "Download" -> {
                    tvAction.text = "Download"
                    tvAction.setBackgroundColor(itemView.context.getColor(android.R.color.holo_blue_light))
                }
                else -> {
                    tvAction.text = "Unknown"
                    tvAction.setBackgroundColor(itemView.context.getColor(android.R.color.darker_gray))
                }
            }
        }
    }

    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<HistoryItem>() {
            override fun areItemsTheSame(oldItem: HistoryItem, newItem: HistoryItem): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: HistoryItem, newItem: HistoryItem): Boolean {
                return oldItem == newItem
            }
        }
    }
}
