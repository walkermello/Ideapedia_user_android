package com.peter.ujian2.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize
import java.time.LocalDateTime
import java.util.Date

@Parcelize
data class DetailIdea(
    @SerializedName("id")
    val id: Long? = null,

    @SerializedName("status")
    val status: String,

    @SerializedName("approvalDate")
    val approvalDate: LocalDateTime? = null, // Menggunakan Date untuk TemporalType.TIMESTAMP

    @SerializedName("rejectedDate")
    val rejectedDate: LocalDateTime? = null, // Menggunakan Date untuk TemporalType.TIMESTAMP

    @SerializedName("comments")
    val comments: String? = null, // Bisa null karena opsional

    @SerializedName("idea")
    val idea: Idea, // Pastikan Anda membuat kelas Idea

    @SerializedName("approvedBy")
    val approvedBy: User // Pastikan Anda membuat kelas User
) : Parcelable
