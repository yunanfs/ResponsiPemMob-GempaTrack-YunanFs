# 🎬 Script Video Penjelasan Kode — GempaTrack

> Tugas **Responsi Mobile I — Paket 1**. Fokus video: **kode**, bukan demo fitur.
> Format narasi: baca bagian **🎙️ Ucapkan** sambil tunjuk baris yang disebut di **👁️ Tunjukkan**.
> Target durasi: **10–14 menit** (silakan potong bagian bertanda *(opsional)*).

---

## 🗺️ Peta Prioritas File

Klasifikasi supaya tidak semua file dijelaskan dengan porsi sama:

| Kelompok | File | Porsi waktu | Alasan |
|---|---|---|---|
| **🔴 WAJIB (inti nilai tugas)** | `HomeViewModel.kt`, `UiState.kt`, `GempaRepository.kt`, `Gempa.kt`, `ApiService.kt`, `GempaTrackNavGraph.kt` | ~70% | Di sini konsep MVVM, StateFlow, sealed state, null safety, Retrofit — poin penilaian utama. |
| **🟡 PENTING (UI khas Compose)** | `HomeScreen.kt`, `DetailScreen.kt` | ~20% | LazyColumn, Scaffold+TopAppBar, state-driven UI (`when`), reusable composable. |
| **🟢 SINGKAT (tema/boilerplate)** | `MainActivity.kt`, `Theme.kt`, `Color.kt`, `Type.kt` | ~10% | Cukup tunjuk peran, jangan bedah warna satu per satu. |
| **⚫ LEWATI (tidak dijelaskan)** | `ic_launcher_*`, `mipmap-*`, `colors.xml`, `themes.xml`, `strings.xml`, `backup_rules.xml`, `AndroidManifest.xml` | 0% | Aset & konfigurasi standar Android Studio. Sebut 1 kalimat kalau perlu ("INTERNET permission ada di manifest"). |

**Urutan penjelasan yang disarankan = alur data dilipat balik (top-down):**
`MainActivity → NavGraph → HomeViewModel → Repository → ApiService/Retrofit → Gempa (model)` lalu balik lagi dari bawah ke atas ke `HomeScreen`. Ini bikin cerita "permintaan data mengalir" mudah diikuti.

---

## 🎬 BAGIAN 1 — Pembuka (≈40 detik)

**🎙️ Ucapkan:**
> "Assalamualaikum / Selamat pagi. Saya [nama], NIM [nim]. Video ini menjelaskan kode aplikasi **GempaTrack**, tugas Responsi Mobile I Paket 1 — aplikasi katalog dan monitoring gempa BMKG.
>
> Stack-nya: **Kotlin**, UI dengan **Jetpack Compose Material 3**, arsitektur **MVVM**, dan networking pakai **Retrofit + Gson** ke REST API BMKG. Data gempa diambil live tanpa API key.
>
> Saya akan jelaskan kode mengikuti **alur data**: dari Activity masuk, ViewModel mengambil data lewat Repository, sampai ditampilkan di Compose. Setelah itu dua layar — Home dan Detail."

**👁️ Tunjukkan:** Struktur folder `app/src/main/java/com/gempatrack/app/` terbuka di IDE (panel Project).

---

## 🎬 BAGIAN 2 — Titik Masuk (≈45 detik) 🟢

**📄 File:** `MainActivity.kt` (24 baris)

**🎙️ Ucapkan:**
> "Mulai dari `MainActivity`. Ini titik masuk aplikasi. Cuma tanggung jawabnya dua: mengaktifkan **edge-to-edge** supaya UI menggambar sampai bawah bilah status, lalu menyusun konten dengan `setContent`.
>
> Di dalam `setContent`, semua dibungkus `GempaTrackTheme` — jadi tema Material 3 kita berlaku untuk seluruh aplikasi — dan isinya satu composable: `GempaTrackNavGraph`, yang mengatur navigasi antar layar."

**👁️ Tunjukkan (pointer hover):**
- `enableEdgeToEdge()` — baris 17
- `setContent { GempaTrackTheme { ... } }` — baris 18–22

**💡 Poin yang ditekankan:** Activity **tidak** memuat logika data sama sekali. Ini prinsip pemisahan tanggung jawab (separation of concerns).

---

## 🎬 BAGIAN 3 — Navigasi 2 Screen (≈1,5 menit) 🔴

**📄 File:** `ui/navigation/GempaTrackNavGraph.kt` (80 baris)

**🎙️ Ucapkan:**
> "File ini mengatur navigasi. Tugas membatasi maksimal 2 screen, jadi ada Home dan Detail."

**👁️ Tunjukkan `sealed class GempaTrackRoute`** (baris 23–26):
> "Route saya bikin sebagai **sealed class** supaya nama rute tidak salah ketik — `Home` = `home`, `Detail` = `detail/{gempaJson}`. Parameter `gempaJson` itu placeholder."
>
> "Kenapa objek gempa dikirim lewat **URL sebagai JSON** dan bukan fetch ulang di Detail? Karena aturan tugas melarang request duplikat — data sudah ada di memori, jadi cukup dibawa. Ada fungsi `gempaDetailRoute` yang mengubah objek `Gempa` jadi JSON lalu di-`Uri.encode` supaya aman jadi bagian URL."

**👁️ Tunjukkan** baris 29–32 (`gempaDetailRoute`).

**👁️ Tunjukkan `GempaTrackNavGraph`** (baris 39–79):
> "Di sini `HomeViewModel` dibuat dengan `viewModel(factory = HomeViewModel.Factory)`. Penting: ViewModel di-scope **di level NavGraph**, bukan di dalam screen Home. Efeknya, saat pindah ke Detail dan balik lagi, **state pencarian tetap tersimpan** — tidak reset. Kalau di-scope di dalam composable Home, dia akan mati saat layar hilang."
>
> "Di dalam `NavHost`, `startDestination` = Home. Pada composable Home, saya **collect** tiga StateFlow dari ViewModel — `uiState`, `searchQuery`, dan `filteredGempa` — pakai `collectAsState()`, lalu kirim sebagai parameter ke `HomeScreen`. Perhatikan: Composable itu **tidak punya logika**, hanya menerima state dan callback."
>
> "Untuk Detail, argumen JSON di-decode balik pakai `Gson().fromJson` di dalam `remember`, dibungkus `runCatching` supaya kalau JSON rusak aplikasi tidak crash — hasilnya `null` dan Detail menampilkan pesan 'data tidak ditemukan'."

**💡 Poin yang ditekankan:**
1. Sealed class untuk rute → type-safe.
2. Data dikirim lewat argumen, bukan fetch ulang.
3. ViewModel di-scope NavGraph → state pencarian persisten.
4. `runCatching` → null-safety saat parsing.

---

## 🎬 BAGIAN 4 — Sealed UiState (≈1 menit) 🔴

**📄 File:** `ui/state/UiState.kt` (14 baris)

**🎙️ Ucapkan:**
> "Ini file kecil tapi kunci. `UiState` adalah **sealed interface** dengan tiga kemungkinan: `Loading`, `Success` dengan data, dan `Error` dengan pesan.
>
> Kenapa sealed? Karena dengan sealed, compiler **tahu semua kemungkinan state** — jadi saat di `when` di UI, harus lengkap. Tidak ada state tak terduga seperti null yang bikin layar kosong misterius. Ini yang disebut **state-driven UI**: layar menggambar berdasarkan state, bukan berdasarkan urutan kejadian."

**👁️ Tunjukkan** seluruh isi file (baris 7–14). Sorot `out T` (variance):
> "`out T` artinya covariant — `Success<List<Gempa>>` tetap dianggap `UiState<List<Gempa>>`. Loading dan Error pakai `Nothing` karena tidak membawa data."

---

## 🎬 BAGIAN 5 — Model data & Null Safety (≈1,5 menit) 🔴

**📄 File:** `data/remote/model/Gempa.kt` (36 baris)

**🎙️ Ucapkan:**
> "Model JSON dari BMKG. Perhatikan strukturnya bercabang: root `GempaResponse` → `Infogempa` → `gempa` yang berupa list. Saya pecah jadi tiga data class sesuai bentuk JSON, dan tiap field diberi `@SerializedName` supaya Gson tahu nama aslinya — contoh `Infogempa` huruf besar.
>
> Yang menarik dari API BMKG: **semua nilai bertipe String**, termasuk `Magnitude`. Jadi kelas `Gempa` semua field-nya `String?` — **nullable**. Kenapa? Karena kalau BMKG mengirim field kosong atau null, aplikasi tidak crash. Ini penerapan **null safety** Kotlin."

**👁️ Tunjukkan** baris 33–35:
> "Dan ini bagian favorit saya: properti turunan `magnitudeValue`. Karena `Magnitude` datang sebagai String `\"5.2\"`, saya sediakan `toDoubleOrNull()`. Kalau bisa diparse, jadi angka `5.2`; kalau tidak, jadi `null` — dan UI menanganinya. Jadi kita dapat angka untuk pewarnaan badge tanpa mengubah tipe aslinya."

**💡 Poin yang ditekankan:** data class, `@SerializedName`, null-safety semua field, extension property turunan.

---

## 🎬 BAGIAN 6 — ApiService & Retrofit (≈1,5 menit) 🔴

**📄 File:** `data/remote/ApiService.kt` (15 baris) + `RetrofitClient.kt` (21 baris)

**🎙️ Ucapkan:**
> "Sekarang lapisan jaringan. `ApiService` cuma sebuah **interface** — kita mendeklarasikan endpoint-nya, bukan menulis kodenya. Satu fungsi: `getGempaterkini` dengan anotasi `@GET(\"DataMKG/TEWS/gempaterkini.json\")`. Karena `suspend`, fungsi ini **non-blocking** — dipanggil dari coroutine, tidak mengunci UI thread. Retrofit-lah yang membuat implementasinya saat runtime."

**👁️ Tunjukkan** baris 12–14.

**👁️ Tunjukkan `RetrofitClient.kt`:**
> "`RetrofitClient` adalah `object` — singleton. Ini yang membangun instance Retrofit: base URL `https://data.bmkg.go.id/`, lalu menambahkan `GsonConverterFactory` supaya JSON otomatis jadi objek Kotlin. Properti `api` memakai `by lazy`, jadi Retrofit baru dibuat **saat pertama dipakai**, dan sekali saja — hemat resource."

**💡 Poin yang ditekankan:** interface + anotasi (deklaratif), `suspend` non-blocking, singleton `object`, `by lazy`, converter Gson.

👉 *Transisi:* "Kedua file ini sekarang hanya diketahui oleh satu pihak: Repository."

---

## 🎬 BAGIAN 7 — Repository (≈1,5 menit) 🔴

**📄 File:** `data/repository/GempaRepository.kt` (28 baris)

**🎙️ Ucapkan:**
> "Repository adalah **satu-satunya tempat** yang memanggil API. ViewModel tidak pernah menyentuh Retrofit langsung — ini inti pola MVVM, biar sumber data terisolasi dan mudah diganti/dites.
>
> Fungsi `getGempaterkini` mengembalikan `Result<List<Gempa>>`. Isinya ekspresi `try/catch`, dan di sini kuncinya: **setiap jenis kegagalan dipetakan ke pesan yang manusiawi**.
> - `HttpException` artinya server menjawab tapi dengan error — saya baca `e.code()` dan buat pesan 'Server BMKG tidak merespons (kode)'.
> - `IOException` artinya masalah jaringan — pesan 'Tidak ada koneksi internet'.
> - Sisanya, error umum — 'Gagal memuat data gempa'.

Perhatikan juga baris sukses:
> "`response.infogempa?.gempa.orEmpty()` — pakai **safe call** `?.` dan `orEmpty()` supaya kalau JSON tidak sesuai bentuk, hasilnya list kosong, bukan crash. Ini null-safety di jalur data."

**👁️ Tunjukkan** baris 18–27.

**💡 Poin yang ditekankan:** satu pintu akses data, error → pesan ramah pengguna, `Result` sebagai pembawa sukses/gagal, safe call + `orEmpty()`.

---

## 🎬 BAGIAN 8 — HomeViewModel (≈2,5 menit) ⭐ (paling penting)

**📄 File:** `ui/home/HomeViewModel.kt` (69 baris)

**🎙️ Ucapkan:**
> "Ini jantung aplikasi — tempat semua logika state dan pencarian. Perhatikan dua pola **StateFlow**: satu privat yang bisa diubah (`_uiState`), satu publik read-only (`uiState`). Cuma ViewModel yang boleh mengubah state; UI hanya boleh membaca."

**👁️ Tunjukkan** baris 26–30 (`_uiState`, `uiState`, `_searchQuery`, `searchQuery`).

**👁️ Tunjukkan** blok `init { fetchGempaterkini() }` (baris 40–42):

**🎙️ Ucapkan:**
> "Blok `init` otomatis memanggil fetch saat ViewModel dibuat — jadi aplikasi langsung memuat data begitu Home dibuka, belum perlu tombol apa pun."

**👁️ Tunjukkan** fungsi `fetchGempaterkini` (baris 44–52):
> "Fungsi ini bekerja di `viewModelScope` — coroutine yang hidup mengikuti ViewModel, jadi kalau layar ditutup, proses otomatis dibatalkan, tidak bocor. Lagikah — urutannya: set `Loading` dulu supaya spinner muncul, baru panggil repository, lalu hasilnya di-`fold`:
> - `onSuccess` → bungkus jadi `UiState.Success(data)`
> - `onFailure` → bungkus jadi `UiState.Error(pesan)`, lengkap dengan fallback pesan default.
>
> Ini pola yang diminta dosen: satu fungsi menghasilkan UiState dari Result."

**👁️ Tunjukkan** `filteredGempa` (baris 36–38) — ini highlight kedua:
> "Dan bagian ini yang paling menarik: pencarian. Saya **tidak** membuat request baru ke server. `filteredGempa` adalah StateFlow turunan yang menggabungkan `combine(_uiState, _searchQuery)`.
>
> Artinya: setiap kali state data berubah **atau** teks pencarian berubah, fungsi ini otomatis menghitung ulang list yang cocok. Lalu `stateIn` mengubahnya jadi StateFlow yang hanya aktif saat ada subscriber (`WhileSubscribed(5 detik)`) — kalau UI tidak menonton, perhitungan berhenti. Hasil awalnya `emptyList()`.
>
> Jadi filtering berjalan **di memori**, instan, tanpa membebani jaringan. Inilah kenapa search terasa langsung."

**👁️ Tunjukkan** fungsi `filterByWilayah` (baris 58–62):
> "Logika filter-nya: ambil data hanya kalau state-nya `Success` — `as?` safe cast, kalau bukan, kembalikan list kosong. Kalau query kosong, tampilkan semua. Kalau ada query, filter dengan `contains(query, ignoreCase = true)` pada `Wilayah` — jadi huruf besar/kecil tidak masalah."

**👁️ Tunjukkan** `companion object` + `Factory` (baris 64–68):
> "Terakhir, `Factory` dengan `viewModelFactory`. Kita pakai manual DI — ViewModel menerima `GempaRepository` lewat konstruktor, sehingga mudah dites dengan mock. Factory ini yang dipakai NavGraph tadi untuk membuat ViewModel."

**💡 Poin yang ditekankan (sebut eksplisit di video):**
1. Pola `_private` / `public` StateFlow.
2. `viewModelScope` + auto-cancel.
3. Loading → Success/Error lewat `fold`.
4. `combine` + `stateIn` = filter lokal reaktif.
5. `WhileSubscribed` = hemat resources.
6. Manual DI via `Factory`.

---

## 🎬 BAGIAN 9 — HomeScreen (≈2 menit) 🟡

**📄 File:** `ui/home/HomeScreen.kt` (326 baris) — **jangan dibaca semua**, tunjuk bagian kunci saja.

**🎙️ Ucapkan:**
> "Masuk ke UI. `HomeScreen` menerima state dan callback dari ViewModel — murni menampilkan, tidak ada logika data.
>
> Struktur luarnya `Scaffold` dengan `TopAppBar` — judul 'GempaTrack', warnanya diambil dari tema. Isi `Scaffold` berupa `Column`: search bar, lalu konten."

**👁️ Tunjukkan** `when (val state = uiState)` (baris 98–133) — **ini inti state-driven UI**:
> "Di sini kepalanya: satu ekspresi `when` atas UiState.
> - `Loading` → panggil `LoadingContent` (spinner + teks).
> - `Error` → `ErrorContent`, tampilkan pesan dan tombol 'Coba Lagi' yang memanggil `onRetry`.
> - `Success` → kalau list kosong tampilkan `EmptyContent` (beda pesan untuk pencarian tanpa hasil), kalau ada isinya baru `LazyColumn`.
>
> Karena UiState sealed, `when` ini pasti lengkap. Tidak ada layar kosong tanpa alasan."

**👁️ Tunjukkan `LazyColumn`** (baris 116–130):
> "`LazyColumn` hanya merender item yang terlihat — hemat memori. Saya beri `key` unik per item, diambil dari `DateTime` supaya animasi dan scroll stabil, dengan fallback `hashCode`. Tiap item memanggil composable `GempaListItem`."

**👁️ Tunjukkan `GempaListItem`** (baris 170–210):
> "Ini **reusable composable**: satu `Card` berisi baris — di kiri kolom Teks wilayah dan tanggal, di kanan badge magnitudo. `Modifier.weight(1f)` bikin sisi teks mengisi sisa lebar."

**👁️ Tunjukkan `MagnitudeBadge` + `magnitudeColors`** (baris 213–237):
> "Badge warna magnitudo — inilah kenapa `magnitudeValue` tadi berguna. Fungsi `magnitudeColors` memetakan nilai ke empat tingkat: di bawah 4.0 abu hangat, 4–5 peach, 5–6, dan di atas 6.0 oranye lebih pekat. Semua tetap lembut, bukan merah menyala — biar nyaman di mata."

**👁️ Tunjukkan `SearchField`** (baris 139–167) sekilas:
> "Search bar: `OutlinedTextField` dengan ikon kaca pembesar, dan ikon silang yang muncul hanya saat ada teks untuk menghapus pencarian — semuanya memanggil callback ke ViewModel."

*(Opsional, tunjuk singkat `LoadingContent`, `ErrorContent`, `EmptyContent` — sebut perannya saja, jangan baca tiap baris.)*

---

## 🎬 BAGIAN 10 — DetailScreen (≈1 menit) 🟡

**📄 File:** `ui/detail/DetailScreen.kt` (127 baris)

**🎙️ Ucapkan:**
> "Layar Detail menampilkan 7 field wajib. Datanya sudah diterima lewat argumen — tidak ada fetch. Kalau objeknya null, tampil pesan 'Data gempa tidak ditemukan'.
>
> Isinya `Column` yang bisa di-`verticalScroll` supaya muat semua baris di layar kecil, dan setiap field dirender oleh composable `DetailRow` — reusable, terima label dan nilai. Kalau nilainya null, ditampilkan tanda strip '—', bukan kata 'null'. Ini null-safety di level UI."

**👁️ Tunjukkan** `DetailRow` (baris 99–126) dan pemanggilan `DetailRow(label = "...", value = ...)` (baris 86–92).

**👁️ Tunjukkan** `navigationIcon` tombol back (baris 48–54):
> "Di `TopAppBar` ada tombol back yang memanggil `onBack` → `popBackStack()` dari NavGraph tadi."

---

## 🎬 BAGIAN 11 — Tema (≈1 menit) 🟢

**📄 Files:** `ui/theme/Color.kt`, `Theme.kt`, `Type.kt`

**🎙️ Ucapkan:**
> "Terakhir, tema Material 3 — ini juga bagian penilaian: sekurangnya dua style yang di-override.
>
> `Color.kt` berisi palet **burnt orange** — saturasi rendah supaya nyaman dilihat, kontrasnya memenuhi WCAG AA. Warnanya dipisah untuk skema terang dan gelap, plus empat warna badge magnitudo tadi.
>
> `Theme.kt` menyusun dua `ColorScheme` — light dan dark — dan `GempaTrackTheme` memilih otomatis mengikuti pengaturan sistem lewat `isSystemInDarkTheme()`.
>
> `Type.kt` meng-override **tujuh** style tipografi — headline, title, body, sampai label — dengan `letterSpacing` dan `lineHeight` yang lebih lapang. Bukan cuma dua, jadi syarat terpenuhi."

**👁️ Tunjukkan:** `Color.kt` baris 11–51, `Theme.kt` baris 53–63, `Type.kt` baris 14 (`Typography(`) saja.

---

## 🎬 BAGIAN 12 — Penutup + Checklist (≈45 detik)

**🎙️ Ucapkan:**
> "Sebagai rangkuman, alur datanya: **HomeScreen** collect state → **HomeViewModel** panggil **Repository** → **ApiService/Retrofit** ambil JSON BMKG → dibungkus **UiState** → kembali ke UI untuk dirender. Pencarian difilter lokal di ViewModel tanpa request tambahan.
>
> Semua persyaratan tugas terpenuhi: Kotlin dengan data class dan null safety, Compose dengan LazyColumn dan composable reusable, Material 3 dengan tema light/dark dan tujuh override tipografi, Scaffold + TopAppBar, MVVM lengkap dengan sealed UiState dan StateFlow, Retrofit, permission INTERNET, dan tepat dua screen.
>
> Sekian penjelasan kode dari saya. Terima kasih."

**👁️ Tunjukkan:** README.md bagian checklist (baris 166–178) sebagai visual penutup.

---

## ⏱️ Alokasi Waktu Ringkas

| Menit | Bagian |
|---|---|
| 0:00–0:40 | Pembuka + stack |
| 0:40–1:25 | MainActivity |
| 1:25–2:55 | NavGraph (2 screen, JSON arg, scope VM) |
| 2:55–3:55 | UiState (sealed) |
| 3:55–5:25 | Gempa.kt (model, null safety, magnitudeValue) |
| 5:25–6:55 | ApiService + RetrofitClient |
| 6:55–8:25 | Repository (error mapping) |
| 8:25–10:55 | **HomeViewModel** (StateFlow, combine, fold) ⭐ |
| 10:55–12:55 | HomeScreen (when-state, LazyColumn, badge, search) |
| 12:55–13:55 | DetailScreen |
| 13:55–14:55 | Tema |
| 14:55–15:40 | Penutup + checklist |

> Kalau harus dipotong jadi 5–7 menit: **lewati** MainActivity (1 kalimat), Tema (1 kalimat), DetailScreen (singkat). Fokus penuh ke **HomeViewModel + Repository + UiState + NavGraph** — di situ poin MVVM dinilai.

---

## 🛠️ Tips Rekaman

1. **Zoom font IDE** ke ~16–18pt sebelum rekam; badan kode harus terbaca jelas.
2. **Collapse** blok import saat merekam — langsung ke kode, bukan daftar import.
3. **Tunjuk dengan kursor**, jangan baca baris satu per satu — sebut maknanya.
4. Sempatkan **scroll ke `filteredGempa` dan `fetchGempaterkini`** dan berhenti 2–3 detik; ini nilai jual utama.
5. Untuk bagian panjang (`HomeScreen` 326 baris), **jangan scroll dari atas ke bawah** — lompat langsung ke `when`, `LazyColumn`, `MagnitudeBadge`, `SearchField`.
6. Susun IDE dengan panel **Structure** terbuka supaya penonton lihat daftar fungsi.
