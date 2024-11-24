package com.peter.ujian2.model

import com.google.gson.annotations.SerializedName

data class ApiResponseDetailIdea(
    @SerializedName("page_number") val pageNumber: Int,
    @SerializedName("column_name") val columnName: String,
    @SerializedName("size_per_page") val sizePerPage: Int,
    @SerializedName("total_pages") val totalPages: Int,
    @SerializedName("sort") val sort: String,
    @SerializedName("total_items") val totalItems: Int,
    @SerializedName("value") val value: String,
    @SerializedName("content") val content: List<DetailIdea>
)

data class ApiResponseIdea(
    @SerializedName("page_number") val pageNumber: Int,
    @SerializedName("column_name") val columnName: String,
    @SerializedName("size_per_page") val sizePerPage: Int,
    @SerializedName("total_pages") val totalPages: Int,
    @SerializedName("sort") val sort: String,
    @SerializedName("total_items") val totalItems: Int,
    @SerializedName("value") val value: String,
    @SerializedName("content") val content: List<DetailIdea>
)
