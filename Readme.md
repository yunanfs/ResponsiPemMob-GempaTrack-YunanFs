# GempaTrack

Aplikasi Android untuk katalog dan monitoring gempa terkini BMKG, dibangun dengan Kotlin + Jetpack Compose (Material 3), arsitektur MVVM, dan data dari REST API BMKG secara dinamis.

Dibuat untuk tugas Responsi Mobile I — Paket 1: Aplikasi Katalog dan Monitoring Gempa BMKG.

## Fitur

| Fitur | Keterangan |
|---|---|
| **Daftar Gempa Terkini** | Menampilkan 15 gempa terakhir dari API BMKG melalui `LazyColumn` — Tanggal, Magnitudo, Wilayah |
| **Pencarian Wilayah** | Search bar untuk filter lokal (in-memory) berdasarkan nama wilayah, case-insensitive, tanpa request tambahan |
| **Detail Gempa** | Klik item untuk membuka halaman detail: Tanggal, Jam, Koordinat, Magnitudo, Kedalaman, Wilayah, Potensi |
| **Loading & Error State** | Indikator loading saat fetch data; pesan error dan tombol "Coba Lagi" saat terjadi masalah jaringan |
| **Material Design 3** | Tema light/dark dengan custom typography dan palet oranye lembut (burnt orange) |
| **Navigasi 2 Screen** | Home dan Detail menggunakan `navigation-compose`, dengan tombol back yang berfungsi |

## Screenshot

<table>
  <tr>
    <th>Home Screen</th>
    <th>Detail Screen</th>
  </tr>
  <tr>
    <td align="center">
      <img width="280" alt="Home Screen" src="https://github.com/user-attachments/assets/a30bf2c5-158e-426c-991d-a79d91bfb96c" />
    </td>
    <td align="center">
      <img width="280" alt="Detail Screen" src="https://github.com/user-attachments/assets/713aea40-c99f-4027-97bb-e85568e236fe" />
    </td>
  </tr>
</table>


## Arsitektur

**MVVM (Model-View-ViewModel) + Repository**, dengan state-driven UI menggunakan `StateFlow`:

```text
┌─────────────────────────────────────────────────────┐
│  View (Composable)                                   │
│  HomeScreen / DetailScreen                           │
│  └── collect StateFlow (collectAsState)             │
├─────────────────────────────────────────────────────┤
│  ViewModel (HomeViewModel)                           │
│  └── UiState: Loading / Success / Error             │
│  └── searchQuery + filteredGempa (filter lokal)     │
├─────────────────────────────────────────────────────┤
│  Repository (GempaRepository)                        │
│  └── bungkus hasil API menjadi Result / UiState     │
├─────────────────────────────────────────────────────┤
│  Remote (Retrofit + Gson)                            │
│  ApiService → RetrofitClient → data.bmkg.go.id      │
├─────────────────────────────────────────────────────┤
│  Data Model (GempaResponse / Gempa)                  │
└─────────────────────────────────────────────────────┘
```

### Alur Data

1. `HomeViewModel` memanggil `GempaRepository.getGempaterkini()` saat pertama kali dibuka.
2. Repository memanggil `ApiService` menggunakan Retrofit untuk mengambil data dari server BMKG.
3. Hasil dibungkus dalam **`UiState`** berupa `Loading`, kemudian `Success(data)` atau `Error(message)`.
4. Composable `HomeScreen` mengumpulkan `StateFlow` melalui `collectAsState()` dan merender UI berdasarkan state.
5. Pencarian dilakukan melalui filter lokal di ViewModel menggunakan `combine`, sehingga tidak ada request jaringan tambahan.

### Sealed UiState

```kotlin
sealed interface UiState<out T> {
    data object Loading : UiState<Nothing>
    data class Success<T>(val data: T) : UiState<T>
    data class Error(val message: String) : UiState<Nothing>
}
```

## Struktur Proyek

```text
app/src/main/java/com/gempatrack/app/
├── MainActivity.kt
├── data/
│   ├── remote/
│   │   ├── ApiService.kt
│   │   ├── RetrofitClient.kt
│   │   └── model/
│   │       └── Gempa.kt
│   └── repository/
│       └── GempaRepository.kt
├── ui/
│   ├── screen/
│   │   ├── HomeScreen.kt
│   │   └── DetailScreen.kt
│   ├── theme/
│   │   ├── Color.kt
│   │   ├── Theme.kt
│   │   └── Type.kt
│   └── viewmodel/
│       └── HomeViewModel.kt
└── navigation/
    └── AppNavigation.kt
```

## Teknologi yang Digunakan

- Kotlin
- Jetpack Compose
- Material 3
- Navigation Compose
- ViewModel
- StateFlow
- Coroutines
- Retrofit
- Gson
- REST API BMKG

## API

Data gempa diperoleh secara dinamis dari API BMKG.

Endpoint yang digunakan:

```text
https://data.bmkg.go.id/DataMKG/TEWS/gempaterkini.json
```

API tersebut digunakan untuk mengambil informasi gempa terkini yang kemudian ditampilkan pada aplikasi.

## State Management

Aplikasi menggunakan `StateFlow` untuk mengelola kondisi UI.

Terdapat tiga kondisi utama:

```text
Loading
   ↓
Success(data)

atau

Loading
   ↓
Error(message)
```

Ketika data berhasil diterima, daftar gempa ditampilkan pada `HomeScreen`. Ketika terjadi kesalahan jaringan, aplikasi menampilkan pesan error beserta tombol **Coba Lagi** untuk melakukan request ulang.

## Pencarian

Fitur pencarian dilakukan secara lokal berdasarkan nama wilayah.

Alurnya:

```text
Data Gempa dari API
        ↓
   searchQuery
        ↓
Filter berdasarkan wilayah
        ↓
filteredGempa
        ↓
   LazyColumn
```

Pencarian bersifat case-insensitive dan tidak melakukan request API baru setiap kali pengguna mengetik.

## Navigasi

Aplikasi memiliki dua screen utama:

```text
HomeScreen
    │
    │ klik item gempa
    ▼
DetailScreen
    │
    │ tombol back
    ▼
HomeScreen
```

Navigasi menggunakan `navigation-compose`.

## Penanganan Error

Jika request ke API gagal, aplikasi akan:

1. Menampilkan pesan error kepada pengguna.
2. Menampilkan tombol **Coba Lagi**.
3. Mengulangi request ketika tombol tersebut ditekan.

Hal ini memastikan aplikasi tetap dapat digunakan dengan baik ketika koneksi internet mengalami gangguan.

## Tampilan

Aplikasi menggunakan Material Design 3 dengan dukungan:

- Light Theme
- Dark Theme
- Custom Typography
- Palet warna oranye lembut
- Card untuk informasi gempa
- LazyColumn untuk daftar gempa
- Search bar untuk pencarian wilayah

## Cara Menjalankan

1. Clone repository.
2. Buka project menggunakan Android Studio.
3. Pastikan perangkat atau emulator telah tersedia.
4. Pastikan perangkat memiliki koneksi internet.
5. Jalankan aplikasi menggunakan tombol **Run** di Android Studio.

## Persyaratan

- Android Studio
- Kotlin
- Android SDK
- Perangkat Android atau Android Emulator
- Koneksi internet untuk mengambil data dari API BMKG

## Catatan

Data gempa yang ditampilkan bergantung pada ketersediaan API BMKG dan koneksi internet perangkat.

Screenshot pada bagian dokumentasi dapat diganti dengan screenshot aktual dari aplikasi yang berjalan pada emulator atau perangkat Android.

video demo : https://drive.google.com/drive/folders/1v8m-cGWxwcqD54hfyXvkNeffedkqEZqR
