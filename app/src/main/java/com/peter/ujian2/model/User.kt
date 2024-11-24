package com.peter.ujian2.model

import kotlinx.parcelize.Parcelize
import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import java.time.LocalDateTime

@Parcelize
data class User(
	@SerializedName("id")
	val id: Long? = null,

	@SerializedName("username")
	val username: String,

	@SerializedName("nip")
	val nip: String,

	@SerializedName("email")
	val email: String,

	@SerializedName("password")
	val password: String,

	@SerializedName("noHp")
	val noHp: String,

	@SerializedName("img_profile")
	val imgProfile: String? = null,

	@SerializedName("unit_kerja")  // Menyesuaikan dengan nama objek di JSON
	val unitKerja: UnitKerja? = null,

	// Pastikan menggunakan tipe yang sesuai atau konverter khusus untuk LocalDateTime
	@SerializedName("createdAt")
	val createdAt: LocalDateTime? = null, // bisa String jika tidak menggunakan konverter khusus

	@SerializedName("createdBy")
	val createdBy: Long? = null,

	@SerializedName("modifiedAt")
	val modifiedAt: LocalDateTime? = null, // bisa String jika tidak menggunakan konverter khusus

	@SerializedName("modifiedBy")
	val modifiedBy: Long? = null
) : Parcelable
