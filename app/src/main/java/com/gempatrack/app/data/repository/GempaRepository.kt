package com.gempatrack.app.data.repository

import com.gempatrack.app.data.remote.RetrofitClient
import com.gempatrack.app.data.remote.ApiService
import com.gempatrack.app.data.remote.model.Gempa
import java.io.IOException
import retrofit2.HttpException

/**
 * Satu-satunya tempat pemanggilan API (pola MVVM / P5).
 * ViewModel tidak pernah menyentuh Retrofit secara langsung.
 */
class GempaRepository(
    private val apiService: ApiService = RetrofitClient.api
) {

    /** Ambil gempa terkini; hasil dibungkus Result agar mudah jadi UiState. */
    suspend fun getGempaterkini(): Result<List<Gempa>> = try {
        val response = apiService.getGempaterkini()
        Result.success(response.infogempa?.gempa.orEmpty())
    } catch (e: HttpException) {
        Result.failure(Exception("Server BMKG tidak merespons (${e.code()})."))
    } catch (e: IOException) {
        Result.failure(Exception("Tidak ada koneksi internet. Periksa jaringan Anda."))
    } catch (e: Exception) {
        Result.failure(Exception("Gagal memuat data gempa."))
    }
}
