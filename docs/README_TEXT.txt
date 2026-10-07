== GempaTrack ===============================================================
Aplikasi Android katalog & monitoring gempa terkini BMKG.
Kotlin + Jetpack Compose (Material 3) + MVVM + Retrofit.
Tugas: Responsi Mobile I - Paket 1.

== Fitur =====================================================================
[1] Daftar Gempa Terkini  : LazyColumn 15 gempa terakhir (Tanggal,
                            Magnitudo, Wilayah) - tanpa gambar.
[2] Pencarian Wilayah     : Filter lokal (in-memory) case-insensitive.
[3] Detail Gempa          : 7 field (Tanggal, Jam, Koordinat, Magnitudo,
                            Kedalaman, Wilayah, Potensi) + tombol back.
[4] Loading & Error State : Indikator loading + pesan error + tombol
                            "Coba Lagi" (retry).
[5] Material Design 3     : Light/Dark theme + custom typography,
                            palet oranye lembut (burnt orange).

== Screenshot ================================================================
Home: (lihat di sini)
  - TopAppBar oranye "GempaTrack"
  - Search bar "Cari wilayah..."
  - Daftar kartu gempa (M 5.9 CALANG-ACEHJAYA, M 6.1 KODI-NTT, dst.)
Detail: (label - nilai, 7 baris)
  - Tanggal 21 Sep 2026 | Jam 08:41:37 WIB | Koordinat -8.33,120.59
  - Magnitudo 5.3 | Kedalaman 10 km | RUTENG-MANGGARAI-NTT
  - Potensi: Tidak berpotensi tsunami

== API =======================================================================
Base URL : https://data.bmkg.go.id/
Endpoint: DataMKG/TEWS/gempaterkini.json  (tanpa API key)
Struktur : { "Infogempa": { "gempa": [ ... ] } }
Semua nilai bertipe String (termasuk Magnitude).

== Build =====================================================================
./gradlew assembleDebug
APK: app/build/outputs/apk/debug/app-debug.apk
Install: adb install -r <apk>

== Struktur (paket com.gempatrack.app) =======================================
MainActivity.kt
data/remote/ApiService.kt, RetrofitClient.kt, model/Gempa.kt
data/repository/GempaRepository.kt
ui/theme/Color.kt, Theme.kt, Type.kt
ui/state/UiState.kt
ui/navigation/GempaTrackNavGraph.kt
ui/home/HomeScreen.kt, HomeViewModel.kt
ui/detail/DetailScreen.kt

== Persyaratan Tugas (checklist) =============================================
[x] Kotlin: data class, null safety, lambda
[x] Compose + LazyColumn + reusable composable
[x] M3: modif Color.kt, Theme.kt (Light/Dark), Type.kt (7 override)
[x] Scaffold + TopAppBar
[x] Data API BMKG (tanpa gambar)
[x] Search filter lokal wilayah
[x] Loading + Error state + retry
[x] MVVM: View/ViewModel/Repository/ApiService/Model/sealed UiState/StateFlow
[x] Retrofit + converter-gson
[x] INTERNET permission
[x] Maks 2 screen (navigation-compose)