package com.gempatrack.app.data.remote

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

/**
 * Klien Retrofit tunggal (lazy, thread-safe) untuk API BMKG.
 * Hanya memakai library yang diizinkan: retrofit + converter-gson.
 */
object RetrofitClient {

    private const val BASE_URL = "https://data.bmkg.go.id/"

    val api: ApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)
    }
}
