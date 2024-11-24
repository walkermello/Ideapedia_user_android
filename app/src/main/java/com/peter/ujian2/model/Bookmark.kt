package com.peter.ujian2.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize
import java.time.LocalDateTime
import java.util.Date

@Parcelize
data class Bookmark(
    @SerializedName("id")
    val id: Long? = null,

    @SerializedName("user")
    val user: User, // Pastikan kelas User menggunakan @Parcelize juga

    @SerializedName("idea")
    val idea: Idea, // Pastikan kelas Idea menggunakan @Parcelize juga

    @SerializedName("createdAt")
    val createdAt: LocalDateTime? = null // Menggunakan Date untuk menyimpan LocalDateTime
) : Parcelable
