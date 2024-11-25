package com.peter.ujian2.services

import com.peter.ujian2.model.ApiResponseDetailIdea
import com.peter.ujian2.model.ApiResponseIdea
import com.peter.ujian2.model.Bookmark
import com.peter.ujian2.model.BookmarkListResponse
import com.peter.ujian2.model.History
import com.peter.ujian2.model.User
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Query

interface IdeaServices {

    /**
     * Mengambil ide dengan status "Approved".
     * @param start posisi awal untuk pagination.
     * @param sortOrder urutan sort (asc/desc).
     * @param sortField field yang digunakan untuk sort.
     * @param size jumlah data per halaman (default: 5).
     * @return Response berisi ide yang sudah disetujui.
     */
    @GET("detail/{start}/{sort_order}/{sort_field}")
    suspend fun getIdeasByStatusApproved(
        @Path("start") start: Int,
        @Path("sort_order") sortOrder: String,
        @Path("sort_field") sortField: String,
        @Query("size") size: Int = 5,
        @Query("col") col: String = "status",
        @Query("val") value: String = "Approved"
    ): Response<ApiResponseDetailIdea>

    /**
     * Mengambil ide dengan filter pencarian tambahan.
     * @param size jumlah data per halaman (default: 5).
     * @param col nama kolom untuk filter.
     * @param value nilai untuk filter.
     * @param start posisi awal untuk pagination.
     * @param sortOrder urutan sort (asc/desc, default: "asc").
     * @param sortField field yang digunakan untuk sort (default: "id").
     * @param username filter berdasarkan username (opsional).
     * @param title filter berdasarkan judul (opsional).
     * @param description filter berdasarkan deskripsi (opsional).
     * @return Response berisi data ide berdasarkan filter.
     */
    @GET("idea/{start}/{sort_order}/{sort_field}")
    suspend fun getIdeasByParams(
        @Path("start") start: Int,
        @Path("sort_order") sortOrder: String,
        @Path("sort_field") sortField: String,
        @Query("size") size: Int = 5,
        @Query("col") col: String = "judul",
        @Query("val") value: String
    ): Response<ApiResponseDetailIdea>

    @Multipart
    @POST("idea")
    suspend fun uploadFile(
        @Part("judul") judul: RequestBody,
        @Part("deskripsi") deskripsi: RequestBody,
        @Part file: MultipartBody.Part?,
        @Part image: MultipartBody.Part?
    ): Response<ResponseServices>

    @GET("idea/download/{fileId}")
    suspend fun downloadFile(@Path("fileId") fileId: String): Response<ResponseBody>

    @POST("bookmark/{ideaId}")
    suspend fun bookmarkIdea(@Path("ideaId") ideaId: Int): Response<ResponseBody>

    @GET("bookmark/{userId}")
    suspend fun getBookmarkIdea(
        @Path("userId") userId: Long,  // Menggunakan userId
        @Header("Authorization") authHeader: String  // Mengirim token Bearer
    ): Response<List<Bookmark>>  // Mengembalikan List<BookmarkListResponse>

    @GET("user/{userId}")
    suspend fun getUserById(@Path("userId") userId: Long): Response<User>

    @GET("history/{userId}")
    suspend fun getHistory(
        @Path("userId") userId: Long,  // Menggunakan userId
        @Header("Authorization") authHeader: String  // Mengirim token Bearer
    ): Response<List<History>>
}
