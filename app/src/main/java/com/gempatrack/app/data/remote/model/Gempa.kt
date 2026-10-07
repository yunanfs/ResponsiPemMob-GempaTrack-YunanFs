package com.gempatrack.app.data.remote.model

import com.google.gson.annotations.SerializedName

/**
 * Respons API BMKG: https://data.bmkg.go.id/DataMKG/TEWS/gempaterkini.json
 * Struktur: { "Infogempa": { "gempa": [ ... ] } }
 * Semua nilai dari BMKG bertipe String, sehingga field dibuat nullable
 * agar null safety Kotlin terpenuhi.
 */
data class GempaResponse(
    @SerializedName("Infogempa")
    val infogempa: Infogempa? = null
)

data class Infogempa(
    @SerializedName("gempa")
    val gempa: List<Gempa>? = null
)

data class Gempa(
    val Tanggal: String? = null,
    val Jam: String? = null,
    val DateTime: String? = null,
    val Coordinates: String? = null,
    val Lintang: String? = null,
    val Bujur: String? = null,
    val Magnitude: String? = null,
    val Kedalaman: String? = null,
    val Wilayah: String? = null,
    val Potensi: String? = null
) {
    /** Magnitudo sebagai angka (null bila tidak bisa diparse). */
    val magnitudeValue: Double?
        get() = Magnitude?.toDoubleOrNull()
}
