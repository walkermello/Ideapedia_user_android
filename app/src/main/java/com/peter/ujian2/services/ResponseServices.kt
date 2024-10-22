package com.peter.ujian2.services

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import com.peter.ujian2.model.UserItem
import kotlinx.parcelize.Parcelize

@Parcelize
data class ResponseServices(

    @field:SerializedName("message")
    val message: String? = null,

    @field:SerializedName("status")
    val status: Boolean? = null,

) : Parcelable
