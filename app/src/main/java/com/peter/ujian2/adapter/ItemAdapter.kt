    package com.peter.ujian2.adapter

    import android.view.LayoutInflater
    import android.view.View
    import android.view.ViewGroup
    import android.widget.ImageView
    import android.widget.TextView
    import androidx.recyclerview.widget.RecyclerView
    import com.peter.ujian2.R
    import com.peter.ujian2.model.UserItem

    class ItemAdapter(
        private var itemList: List<UserItem>,
        private val onEdit: (UserItem) -> Unit,
        private val onDelete: (UserItem) -> Unit
    ) : RecyclerView.Adapter<ItemAdapter.ViewHolder>() {

        // Buat list untuk menyimpan status bookmark
        private val bookmarkedItems = mutableSetOf<Int>()

        inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            val btnBookmark: ImageView = itemView.findViewById(R.id.btnBookmark)
            val txtNama: TextView = itemView.findViewById(R.id.txtNama)
            val txtHutang: TextView = itemView.findViewById(R.id.txtHutang)
            val txtAlamat: TextView = itemView.findViewById(R.id.txtAlamat)
            val btnEdit: ImageView = itemView.findViewById(R.id.btnEdit) // Pastikan ada elemen untuk tombol edit
            val btnDelete: ImageView = itemView.findViewById(R.id.btnDelete) // Pastikan ada elemen untuk tombol delete

            fun bind(item: UserItem) {
                txtNama.text = item.nama
                txtHutang.text = item.hutang.toString()
                txtAlamat.text = item.alamat

                // Cek apakah item ini sudah di-bookmark
                if (bookmarkedItems.contains(adapterPosition)) {
                    btnBookmark.setImageResource(R.drawable.star_filled) // Ganti dengan drawable untuk bintang berisi
                } else {
                    btnBookmark.setImageResource(R.drawable.star) // Bintang transparan
                }

                btnBookmark.setOnClickListener {
                    // Toggle bookmark status
                    if (bookmarkedItems.contains(adapterPosition)) {
                        bookmarkedItems.remove(adapterPosition)
                        btnBookmark.setImageResource(R.drawable.star) // Bintang transparan
                    } else {
                        bookmarkedItems.add(adapterPosition)
                        btnBookmark.setImageResource(R.drawable.star_filled) // Bintang berisi
                    }
                }
            }
        }

        // Fungsi untuk memfilter item
        fun filter(query: String?) {
            itemList = if (query.isNullOrEmpty()) {
                itemList // Kembalikan daftar asli jika query kosong
            } else {
                itemList.filter { item ->
                    item.nama?.contains(query, ignoreCase = true) == true // Filter berdasarkan nama
                }
            }
            notifyDataSetChanged() // Perbarui tampilan
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.item_list, parent, false)
            return ViewHolder(view)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val user = itemList[position]
            holder.bind(user) // Memanggil metode bind

            // Set listener untuk tombol edit
            holder.btnEdit.setOnClickListener {
                onEdit(user)
            }

            // Set listener untuk tombol delete
            holder.btnDelete.setOnClickListener {
                onDelete(user)
            }
        }

        override fun getItemCount(): Int = itemList.size

        // Metode untuk memperbarui data di adapter
        fun updateData(newItems: List<UserItem>) {
            itemList = newItems // Update data asli
            notifyDataSetChanged() // Memberi tahu adapter untuk memperbarui tampilan
        }

        fun removeItem(userItem: UserItem) {
            itemList = itemList.filter { it.id != userItem.id } // Hapus item berdasarkan ID
            notifyDataSetChanged() // Perbarui tampilan
        }
    }
