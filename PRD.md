# PRD — Katalog & Monitoring Gempa BMKG

**Dokumen:** Product Requirements Document (PRD)
**Versi:** 1.0
**Tanggal:** 6 Oktober 2026
**Sumber requmen:** `Responsi Mobile I.docx` — Paket 1
**Status:** Draft untuk review

---

## 1. Ringkasan Produk

Aplikasi mobile Android berbahasa Indonesia yang menampilkan katalog gempa terkini dari REST API BMKG. Pengguna dapat melihat daftar gempa, mencari berdasarkan wilayah, dan membuka detail satu gempa. Dibangun dengan Kotlin + Jetpack Compose, arsitektur MVVM, tanpa API key, tanpa library gambar.

**Satu kalimat:** *Aplikasi Android (Kotlin/Compose) yang menampilkan, mencari, dan mendetailkan data gempa terkini dari `https://data.bmkg.go.id/DataMKG/TEWS/gempaterkini.json`.*

## 2. Latar Belakang & Masalah

- Indonesia berada di zona cincin api; aktivitas gempa sering terjadi.
- Pengguna membutuhkan cara mudah melihat, mencari, dan mengeksplorasi informasi gempa terkini dari perangkat mobile.
- Saat ini informasi tersebar di situs/siaran; belum ada tampilan ringkas sekali-klik di HP.
- Proyek ini juga menjadi sarana penerapan konsep Mobile Programming: state-driven UI, recomposition, networking, MVVM.

## 3. Tujuan

| ID | Tujuan | Ukuran keberhasilan |
|----|--------|---------------------|
| G1 | Menampilkan daftar gempa terkini dari API BMKG | 15 entri terakhir tampil, tanpa gambar |
| G2 | Pencarian berdasarkan wilayah | Filter lokal instan, tanpa request tambahan |
| G3 | Detail lengkap satu gempa | 7 field wajib tampil, tombol back berfungsi |
| G4 | State-driven UI | Loading, Error, Success semuanya punya UI sendiri |
| G5 | Arsitektur rapi (MVVM + P5) | Tidak ada panggilan API langsung di Composable |

## 4. Target Pengguna

- **Primer:** masyarakat umum / warga yang ingin cek gempa terkini dengan cepat.
- **Sekunder:** dosen penguji Responsi (menilai pemahaman konsep Mobile Programming).

Karakteristik: koneksi internet tidak selalu stabil, butuh info cepat, layar HP kecil.

## 5. Ruang Lingkup (In Scope)

**Fitur wajib (berasal dari dokumen tugas):**

| ID | Fitur | Prioritas | Keterangan |
|----|-------|-----------|------------|
| F1 | Home: judul aplikasi, search bar, daftar gempa | P0 | Tanggal + Magnitudo + Wilayah |
| F2 | LazyColumn untuk daftar | P0 | Hanya LazyColumn, tanpa gambar |
| F3 | Search/filter lokal per Wilayah | P0 | Case-insensitive, substring |
| F4 | Loading state | P0 | Indikator saat fetch berjalan |
| F5 | Error state | Pesan + opsi coba lagi (retry) | P0 |
| F6 | Detail Screen: 7 field | P0 | Tanggal, Jam, Coordinates, Magnitudo, Kedalaman, Wilayah, Potensi |
| F7 | Navigasi Home → Detail dan tombol back | P0 | navigation-compose, maks 2 screen |
| F8 | Material Design 3 theme + typography | P0 | Color.kt, Theme.kt (light/dark), Type.kt (≥2 override) |
| F9 | Scaffold + TopAppBar | P0 | — |
| F10 | INTERNET permission | P0 | AndroidManifest.xml |

**Out of Scope (sengaja tidak dikerjakan):**

- Library gambar (Coil/Glide) — dilarang.
- Peta, share, notifikasi push, bookmark/favorit, filter lanjutan (magnitude, kedalaman).
- API key, login, database lokal/room, offline cache.
- Screen tambahan > 2 (riwayat, pengaturan, about).
- Data selain `gempaterkini.json` (gempa-terkini, dirasakan, dll.).

## 6. Kebutuhan Fungsional (FR)

**FR-01 — Ambil data gempa**
- Sumber: `GET https://data.bmkg.go.id/DataMKG/TEWS/gempaterkini.json`, tanpa API key, tanpa header auth.
- Root JSON: `Infogempa.gempa[]` (array).
- Dipanggil saat Home Screen pertama kali dimuat (dan saat retry).

**FR-02 — Tampilkan daftar gempa (Home)**
- Kolom wajib per item: **Tanggal**, **Magnitude**, **Wilayah**.
- Urutan: sesuai respons API (terbaru lebih dulu).
- Tanpa gambar apa pun.

**FR-03 — Pencarian wilayah**
- Input: teks pada search bar di Home.
- Filter lokal (in-memory) pada field `Wilayah`, case-insensitive, cocok sebagian (contains).
- Tidak memicu request jaringan.
- Hasil kosong → tampil pesan "Tidak ada gempa ditemukan".

**FR-04 — Pindah ke detail**
- Klik item daftar → Detail Screen, data gempa terpilih dibawa lewat argumen navigation (String/serialized), bukan fetch ulang.

**FR-05 — Tampilkan detail**
- Field wajib: Tanggal, Jam, Coordinates, Magnitudo, Kedalaman, Wilayah, Potensi.
- Tombol back di TopAppBar → kembali ke Home (state search dipertahankan).

**FR-06 — Kelola state UI**
- `sealed interface UiState` berisi: `Loading`, `Success(data)`, `Error(message)`.
- Dikirim via `StateFlow` dari ViewModel ke Composable.
- Composable hanya merender state; tidak memanggil repository/API.

**FR-07 — Tema & tipografi**
- Material 3 dynamic/light/dark ColorScheme lewat `Theme.kt`.
- Wajib modifikasi `Color.kt`, `Theme.kt`, `Type.kt` (minimal 2 typografi override).
- `Scaffold` + `TopAppBar` dipakai di kedua screen.

## 7. Kebutuhan Non-Fungsional (NFR)

| ID | Kategori | Requmen |
|----|----------|---------|
| N1 | Performa | Daftar tidak blank saat load; recomposition tidak memicu fetch ulang |
| N2 | Ketahanan | Gagal jaringan → UI Error jelas + bisa coba lagi, tidak crash |
| N3 | Null safety | Seluruh model Kotlin null-safe; field API yang bisa null ditangani |
| N4 | Layar | Dukung portrait; responsif di lebar 360dp+ |
| N5 | Dark mode | Ikut sistem (light/dark ColorScheme aktif) |
| N6 | Bahasa | UI berbahasa Indonesia |
| N7 | Kompatibilitas | minSdk sesuai default project Compose (24+), targetSdk terbaru |

## 8. Spesifikasi API

**Endpoint:** `https://data.bmkg.go.id/DataMKG/TEWS/gempaterkini.json`
**Metode:** GET, tanpa API key.
**Respons (dikonfirmasi live, 15 entri):**

```json
{
  "Infogempa": {
    "gempa": [
      {
        "Tanggal": "04 Okt 2026",
        "Jam": "06:37:31 WIB",
        "DateTime": "2026-10-03T23:37:31+00:00",
        "Coordinates": "4.56,94.97",
        "Lintang": "4.56 LU",
        "Bujur": "94.97 BT",
        "Magnitude": "5.9",
        "Kedalaman": "26 km",
        "Wilayah": "68 km BaratDaya CALANG-ACEHJAYA",
        "Potensi": "Tidak berpotensi tsunami"
      }
    ]
  }
}
```

**Catatan penting:**
- Semua nilai bertipe **String** (termasuk `Magnitude`) — parsing ke `Double` harus aman (`toDoubleOrNull()`).
- `Coordinates` format `"lintang,bujur"` — tampilkan apa adanya di detail.
- Kelas data memetakan persis key API (pakai `@SerializedName` bila nama properti Kotlin berbeda).

**Model data (data class):**

```kotlin
data class GempaResponse(val Infogempa: Infogempa)
data class Infogempa(val gempa: List<Gempa>)
data class Gempa(
    val Tanggal: String?, val Jam: String?, val DateTime: String?,
    val Coordinates: String?, val Lintang: String?, val Bujur: String?,
    val Magnitude: String?, val Kedalaman: String?, val Wilayah: String?,
    val Potensi: String?
)
```

## 9. Arsitektur

**Pola:** MVVM + Repository + StateFlow (`UiState` sealed), sesuai pola P5.

```
View (Composable)  ──collect──  ViewModel (StateFlow<UiState>)
        │                              │
   tidak call API                 panggil Repository
                                          │
                                   Repository ──> ApiService (Retrofit)
                                          │
                                   Data Model (Gson)
```

**Struktur paket yang direkomendasikan:**

```
id.ac.<kampus>.gempabmkg/
├── data/
│   ├── remote/
│   │   ├── ApiService.kt        // suspend fun getGempaterkini()
│   │   ├── RetrofitClient.kt    // base URL + converter-gson
│   │   └── model/Gempa.kt       // data class response
│   └── repository/
│       └── GempaRepository.kt   // wrap hasil jadi Result/UiState
├── ui/
│   ├── theme/                   // Color.kt, Theme.kt, Type.kt
│   ├── navigation/NavGraph.kt   // 2 route: home, detail/{id}
│   ├── home/
│   │   ├── HomeScreen.kt
│   │   └── HomeViewModel.kt     // UiState + search query
│   └── detail/
│       └── DetailScreen.kt
└── MainActivity.kt
```

**Library yang DIIZINKAN saja:**
`retrofit`, `converter-gson`, `navigation-compose`, `lifecycle-viewmodel-compose`.
Tanpa Coil, Glide, Hilt, Coroutines library tambahan (kotlinx-coroutines sudah ikut Compose lifecycle — gunakan `viewModelScope` + suspend dari Retrofit).

## 10. UX Flow & State Diagram

**Alur pengguna:**

```
Buka App ──> Home (TopAppBar "Gempa BMKG Terkini" + SearchBar + LazyColumn)
                 │ Loading → CircularProgressIndicator
                 │ Error   → teks error + tombol "Coba Lagi"
                 │ Success → 15 item [Tanggal | Magnitudo | Wilayah]
                 │            └─ ketik di search → filter lokal wilayah
                 └─ klik item ──> Detail (7 field + back arrow)
```

**State UI:**

```kotlin
sealed interface UiState<out T> {
    data object Loading : UiState<Nothing>
    data class Success<T>(val data: T) : UiState<T>
    data class Error(val message: String) : UiState<Nothing>
}
```

State terpisah di dua tempat:
- `uiState: StateFlow<UiState<List<Gempa>>>` → hasil fetch.
- `searchQuery: StateFlow<String>` → input pencarian, difilter di ViewModel (`combine`) sehingga UI murni state-driven.

**Spesifikasi layar:**

| Layar | Elemen | Aksi |
|-------|--------|------|
| Home | `Scaffold` + `TopAppBar` judul; search field; `LazyColumn` | ketik (filter), klik item → detail |
| Detail | `TopAppBar` + back icon; 7 baris label: nilai | back → Home |

## 11. Kriteria Penerimaan (Acceptance Criteria)

- [ ] APK debug bisa diinstal dan terbuka tanpa crash.
- [ ] Daftar gempa tampil dari API (bukan data dummy/hardcoded).
- [ ] Setiap item daftar memuat Tanggal, Magnitudo, Wilayah; tidak ada gambar.
- [ ] Mencari wilayah memfilter daftar secara lokal dan instan; mengosongkan input mengembalikan daftar penuh.
- [ ] Klik item membuka Detail dengan 7 field lengkap; tombol back kembali ke Home.
- [ ] Loading indicator tampil selama fetch; simulasi jaringan mati menampilkan Error state + retry, app tidak crash.
- [ ] `AndroidManifest.xml` memuat `android.permission.INTERNET`.
- [ ] Tidak ada panggilan API/repository di dalam `@Composable`.
- [ ] `Color.kt`, `Theme.kt` (light/dark ColorScheme), `Type.kt` (≥2 override) dimodifikasi; `Scaffold` + `TopAppBar` dipakai.
- [ ] Hanya 2 screen, hanya library yang diizinkan.

## 12. Deliverable Submission

| # | Item | Ketentuan |
|---|------|-----------|
| 1 | Repository GitHub | Public, berisi source code |
| 2 | `README.md` | Screenshot 2 screen, daftar fitur, diagram arsitektur, dokumentasi API, catatan teknis |
| 3 | APK debug | `app-debug.apk` bisa diinstal |
| 4 | Video penjelasan kode | Fokus penjelasan kode, bukan demo aplikasi |
| 5 | Link GitHub + video | Diisi pada form yang disediakan |

**Deadline:** 24 jam setelah shift selesai (pengumpulan offline saat shift).

## 13. Milestone / Rencana Kerja

| Fase | Isi | Target |
|------|-----|--------|
| M1 | Setup project Compose + dependency + INTERNET permission | Hari 0 |
| M2 | Data layer: model, ApiService, Retrofit, Repository | Hari 0 |
| M3 | ViewModel + UiState + pencarian lokal | Hari 1 |
| M4 | UI: theme (Color/Theme/Type), HomeScreen (Scaffold, TopAppBar, LazyColumn) | Hari 1 |
| M5 | DetailScreen + navigation-compose + back | Hari 1 |
| M6 | Uji state (loading/error/offline), rapikan, build APK debug | Hari 2 |
| M7 | README + screenshot + video penjelasan kode + push GitHub | Hari 2 (H-24 jam) |

## 14. Risiko & Mitigasi

| Risiko | Dampak | Mitigasi |
|--------|--------|----------|
| API BMKG lambat/tidak stabil | Loading lama, error | Loading state jelas, retry, timeout wajar |
| Field API null/kosong | Crash | Data class nullable + fallback teks "—" |
| `Magnitude` string, bukan number | Format salah | `toDoubleOrNull()` untuk warna/sorting, tampil sebagai teks |
| Batasan library ketat | Tergoda pakai lib tambahan | Hanya 4 library yang diizinkan |
| Waktu submission mepet | Kumpul terlambat | Ikuti milestone, prioritaskan M1–M5 dulu |

## 15. Keputusan Desain (Final)

| Topik | Keputusan |
|-------|-----------|
| Nama aplikasi | **GempaTrack** (judul TopAppBar + README) |
| Package name | `com.gempatrack.app` |
| Warna brand | Oranye bertema gempa yang **tidak mencolok mata** — nada terakota/burnt orange, saturasi rendah-sedang, kontras WCAG AA. Bukan oranye neon/oranye cerah.

**Palet warna (dari `Color.kt`):**

| Token | Light | Dark | Keterangan |
|-------|-------|------|-----------|
| `Primary` | `#A8481B` | `#FFB68D` | burnt orange, lembut |
| `OnPrimary` | `#FFFFFF` | `#5A1B00` | — |
| `PrimaryContainer` | `#FFDBC4` | `#7C3312` | peach redup |
| `OnPrimaryContainer` | `#2B0F00` | `#FFDBC4` | — |
| `Secondary` | `#77574A` | `#E7BDB0` | cokelat hangat netral |
| `Background` | `#FFF8F4` | `#201A17` | off-white hangat / gelap hangat |
| `Surface` | `#FFF8F4` | `#2A211D` | — |
| `SurfaceVariant` | `#F5DED3` | `#51443E` | — |

Aturan: tidak ada warna saturasi penuh di area luas; oranye hanya untuk aksen (ikon, badge magnitudo, highlight). Badge magnitudo memakai gradasi nilai rendah→tinggi tetap dalam rentang hangat yang nyaman dilihat.

---

*PRD ini disusun dari dokumen `Responsi Mobile I.docx` dan diverifikasi langsung terhadap endpoint BMKG (6 Okt 2026). Perubahan kebutuhan harap ditandai dengan merilis versi baru dokumen ini.*
