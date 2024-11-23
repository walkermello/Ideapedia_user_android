package com.peter.ujian2.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.peter.ujian2.R
import com.peter.ujian2.model.HistoryItem

class HistoryAdapter : RecyclerView.Adapter<HistoryAdapter.HistoryViewHolder>() {

    // Data dummy langsung di dalam HistoryAdapter
    private val historyList: List<HistoryItem> = listOf(
        HistoryItem(
            id = 1,
            date = "2024-10-01",
            title = "Uploaded Document",
            profile = "John Doe",
            action = "upload",
            status = "completed"
        ),
        HistoryItem(
            id = 2,
            date = "2024-10-02",
            title = "Deleted Image",
            profile = "Jane Smith",
            action = "delete",
            status = "pending"
        ),
        HistoryItem(
            id = 3,
            date = "2024-10-03",
            title = "Downloaded Report",
            profile = "Alice Johnson",
            action = "download",
            status = "completed"
        ),
        HistoryItem(
            id = 4,
            date = "2024-10-04",
            title = "Updated Profile",
            profile = "Bob Williams",
            action = "upload",
            status = "completed"
        ),
        HistoryItem(
            id = 5,
            date = "2024-10-05",
            title = "Deleted Old File",
            profile = "Chris Brown",
            action = "delete",
            status = "completed"
        )
    )

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistoryViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_history, parent, false)
        return HistoryViewHolder(view)
    }

    override fun onBindViewHolder(holder: HistoryViewHolder, position: Int) {
        val historyItem = historyList[position]
        holder.bind(historyItem)
    }

    override fun getItemCount(): Int = historyList.size

    class HistoryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvDate: TextView = itemView.findViewById(R.id.tvDate)
        private val tvMonth: TextView = itemView.findViewById(R.id.tvMonth)
        private val tvYear: TextView = itemView.findViewById(R.id.tvYear)
        private val tvTitle: TextView = itemView.findViewById(R.id.tvTitle)
        private val tvProfile: TextView = itemView.findViewById(R.id.tvProfile)
        private val tvAction: TextView = itemView.findViewById(R.id.tvAction)
        private val tvStatus: TextView = itemView.findViewById(R.id.tvStatus)

        fun bind(item: HistoryItem) {
            // Pisahkan tanggal menjadi hari, bulan, dan tahun
            val dateParts = item.date.split("-")
            if (dateParts.size == 3) {
                tvDate.text = dateParts[2] // Hari
                tvMonth.text = dateParts[1] // Bulan
                tvYear.text = dateParts[0] // Tahun
            }

            tvTitle.text = item.title
            tvProfile.text = item.profile
            tvStatus.text = item.status

            // Mengubah warna background tvAction berdasarkan jenis aksi
            when (item.action) {
                "upload" -> {
                    tvAction.text = "Upload"
                    tvAction.setBackgroundColor(itemView.context.getColor(android.R.color.holo_green_light))
                }
                "delete" -> {
                    tvAction.text = "Delete"
                    tvAction.setBackgroundColor(itemView.context.getColor(android.R.color.holo_red_light))
                }
                "download" -> {
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
}
