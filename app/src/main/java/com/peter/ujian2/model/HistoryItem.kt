package com.peter.ujian2.model

import java.time.LocalDateTime

data class HistoryItem(
    val id: Long?,              // Unique ID untuk setiap item
    val date: LocalDateTime?,         // Tanggal riwayat
    val title: String,        // Judul riwayat
    val profile: String,      // Profil atau nama pengguna terkait
    val action: String,       // Jenis aksi seperti "upload", "delete", atau "download"
    val status: String        // Status item riwayat, misalnya "completed" atau "pending"
)
