package com.example.catapi.network

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Query

private const val BASE_URL = "https://api.thecatapi.com/v1/"

private val moshi = Moshi.Builder()
    .add(KotlinJsonAdapterFactory())
    .build()

private val retrofit = Retrofit.Builder()
    .addConverterFactory(MoshiConverterFactory.create(moshi))
    .baseUrl(BASE_URL)
    .build()

interface CatApiService {
    @GET("images/search")
    @Headers(
        "x-api-key: 5e8995e8-09d2-42dd-8e21-1dccdd19edd7",
        "Content-Type: application/json"
    )

    suspend fun getCats(
        @Query("limit") limit: Int,
        @Query("page") page: Int,
        @Query("order") order: String = "asc",
        @Query("mime_types") type: String = "jpg"
    ): List<Cat>
}

object CatApi {
    val retrofitService: CatApiService by lazy {
        retrofit.create(CatApiService::class.java)
    }
}

enum class CatApiStatus { LOADING, ERROR, DONE }
