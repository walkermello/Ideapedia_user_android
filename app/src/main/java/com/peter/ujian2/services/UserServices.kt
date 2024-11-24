//package com.peter.ujian2.services
//
//import com.peter.ujian2.model.User
//import okhttp3.MultipartBody
//import okhttp3.RequestBody
//import retrofit2.Call
//import retrofit2.http.Body
//import retrofit2.http.DELETE
//import retrofit2.http.Field
//import retrofit2.http.FieldMap
//import retrofit2.http.FormUrlEncoded
//import retrofit2.http.GET
//import retrofit2.http.Multipart
//import retrofit2.http.POST
//import retrofit2.http.PUT
//import retrofit2.http.Part
//import retrofit2.http.Path
//import retrofit2.http.Query
//import retrofit2.Response
//
//interface UserServices {
//    @Multipart
//    @POST("ujian/add")
//    fun addUser (@Part("nama") nama: RequestBody,
//                 @Part("alamat") alamat : RequestBody,
//                 @Part("hutang") hutang : RequestBody,
//    ): Call<ResponseServices>
//
//    @GET("ujian/all")
//    suspend fun getAllUser(@Query("start") start: Int, @Query("limit") limit: Int): Response<User>
//
//    @GET("ujian/all")
//    suspend fun getUserByName(
//        @Query("filters[0][lg]") logicalOperator: String = "AND",
//        @Query("filters[0][co][0][fl]") field: String = "nama",
//        @Query("filters[0][co][0][op]") operator: String = "like",
//        @Query("filters[0][co][0][vl]") value: String? = null,
//        @Query("start") start: Int = 0,
//        @Query("limit") limit: Int = 5
//    ): Response<User>
//
//
//    @FormUrlEncoded
//    @POST("ujian/update")
//    fun updateUser(
//        @Field("id") userId: Int,
//        @Field("nama") nama: String,
//        @Field("alamat") alamat: String,
//        @Field("hutang") hutang: Int
//    ): Call<ResponseServices>
//
//    // Tambahkan fungsi untuk delete data user
//    @POST("ujian/delete")
//    fun deleteUser(
//        @Body body: Map<String, Int> // Mengambil ID dari body request
//    ): Call<ResponseServices>
//}