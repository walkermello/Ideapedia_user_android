package com.peter.ujian2.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize
import java.time.LocalDateTime

@Parcelize
data class History(
    @SerializedName("id")
    val id: Long? = null,

    @SerializedName("user")
    val user: User, // Pastikan Anda membuat kelas User

    @SerializedName("idea")
    val idea: Idea, // Pastikan Anda membuat kelas Idea

    @SerializedName("action")
    val action: String,

    @SerializedName("detailAction")
    val detailAction: String? = null,

    @SerializedName("createdAt")
    val createdAt: LocalDateTime? = null,

    @SerializedName("modifiedAt")
    val modifiedAt: LocalDateTime? = null,

    @SerializedName("modifiedBy")
    val modifiedBy: Long? = null
) : Parcelable
