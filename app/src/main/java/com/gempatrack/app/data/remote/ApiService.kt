package com.gempatrack.app.data.remote

import com.gempatrack.app.data.remote.model.GempaResponse
import retrofit2.http.GET

/**
 * REST API BMKG — tanpa API key.
 * Base URL: https://data.bmkg.go.id
 */
interface ApiService {

    /** Daftar 15 gempa terkini. */
    @GET("DataMKG/TEWS/gempaterkini.json")
    suspend fun getGempaterkini(): GempaResponse
}
