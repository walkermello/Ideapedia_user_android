package com.peter.ujian2.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize
import java.time.LocalDateTime

@Parcelize
data class UnitKerja(
    @SerializedName("id")
    val id: Long? = null,

    @SerializedName("unitName")
    val unitName: String,

    @SerializedName("isAdmin")
    val isAdmin: Boolean,

    @SerializedName("createdAt")
    val createdAt: LocalDateTime? = null,

    @SerializedName("createdBy")
    val createdBy: Long? = null,

    @SerializedName("modifiedAt")
    val modifiedAt: LocalDateTime? = null,

    @SerializedName("modifiedBy")
    val modifiedBy: Long? = null
) : Parcelable
