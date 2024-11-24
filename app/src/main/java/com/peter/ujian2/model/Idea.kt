package com.peter.ujian2.model

import kotlinx.parcelize.Parcelize
import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import java.time.LocalDateTime

@Parcelize
data class Idea(

	@SerializedName("id")
	val id: Long? = null,

	@SerializedName("judul")
	val judul: String,

	@SerializedName("deskripsi")
	val deskripsi: String,

	@SerializedName("pengujiPertama")
	val pengujiPertama: Long,

	@SerializedName("pengujiKedua")
	val pengujiKedua: Long,

	@SerializedName("pengujiKetiga")
	val pengujiKetiga: Long,

	@SerializedName("feedback")
	val feedback: String? = null,

	@SerializedName("fileName")
	val fileName: String? = null,

	@SerializedName("filePath")
	val filePath: String? = null,

	@SerializedName("fileImage")
	val fileImage: String? = null,

	@SerializedName("createdAt")
	val createdAt: LocalDateTime? = null,

	@SerializedName("modifiedAt")
	val modifiedAt: LocalDateTime? = null,

	@SerializedName("modifiedBy")
	val modifiedBy: Long? = null,

	@SerializedName("user")
	val user: User
) : Parcelable
