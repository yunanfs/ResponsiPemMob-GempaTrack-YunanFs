@file:OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)

package com.gempatrack.app.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.gempatrack.app.data.remote.model.Gempa
import com.gempatrack.app.ui.state.UiState
import com.gempatrack.app.ui.theme.MagnitudeCalm
import com.gempatrack.app.ui.theme.MagnitudeCalmOn
import com.gempatrack.app.ui.theme.MagnitudeModerate
import com.gempatrack.app.ui.theme.MagnitudeModerateOn
import com.gempatrack.app.ui.theme.MagnitudeSevere
import com.gempatrack.app.ui.theme.MagnitudeSevereOn
import com.gempatrack.app.ui.theme.MagnitudeStrong
import com.gempatrack.app.ui.theme.MagnitudeStrongOn

/**
 * Home Screen: judul aplikasi + search bar + daftar gempa (LazyColumn).
 * Semua tampilan ditentukan oleh [uiState] (state-driven UI).
 */
@Composable
fun HomeScreen(
    uiState: UiState<List<Gempa>>,
    searchQuery: String,
    gempaList: List<Gempa>,
    onSearchQueryChange: (String) -> Unit,
    onRetry: () -> Unit,
    onGempaClick: (Gempa) -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = { Text(text = "GempaTrack", fontWeight = FontWeight.SemiBold) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
        ) {
            SearchField(
                query = searchQuery,
                onQueryChange = onSearchQueryChange,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp)
            )

            when (val state = uiState) {
                UiState.Loading -> LoadingContent(
                    modifier = Modifier.fillMaxSize()
                )

                is UiState.Error -> ErrorContent(
                    message = state.message,
                    onRetry = onRetry,
                    modifier = Modifier.fillMaxSize()
                )

                is UiState.Success -> {
                    if (gempaList.isEmpty()) {
                        EmptyContent(
                            query = searchQuery,
                            modifier = Modifier.fillMaxSize()
                        )
                    } else {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 4.dp, bottom = 16.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            items(
                                items = gempaList,
                                key = { gempa -> gempa.DateTime ?: gempa.hashCode().toString() }
                            ) { gempa ->
                                GempaListItem(
                                    gempa = gempa,
                                    onClick = { onGempaClick(gempa) }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

/** Search bar untuk filter lokal wilayah. */
@Composable
private fun SearchField(
    query: String,
    onQueryChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    OutlinedTextField(
        value = query,
        onValueChange = onQueryChange,
        modifier = modifier,
        placeholder = { Text(text = "Cari wilayah...") },
        leadingIcon = {
            Icon(imageVector = Icons.Default.Search, contentDescription = "Cari wilayah")
        },
        trailingIcon = {
            if (query.isNotEmpty()) {
                IconButton(onClick = { onQueryChange("") }) {
                    Icon(imageVector = Icons.Default.Close, contentDescription = "Hapus pencarian")
                }
            }
        },
        singleLine = true,
        shape = RoundedCornerShape(16.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = MaterialTheme.colorScheme.primary,
            cursorColor = MaterialTheme.colorScheme.primary
        )
    )
}

/** Item daftar: Tanggal + Magnitudo + Wilayah, tanpa gambar. */
@Composable
private fun GempaListItem(
    gempa: Gempa,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = gempa.Wilayah ?: "Wilayah tidak tersedia",
                    style = MaterialTheme.typography.titleMedium,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = gempa.Tanggal ?: "-",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            MagnitudeBadge(
                magnitude = gempa.Magnitude,
                magnitudeValue = gempa.magnitudeValue
            )
        }
    }
}

/** Badge magnitudo dengan gradasi warna hangat yang nyaman dilihat. */
@Composable
private fun MagnitudeBadge(
    magnitude: String?,
    magnitudeValue: Double?
) {
    val (background, content) = magnitudeColors(magnitudeValue)
    Box(
        modifier = Modifier
            .background(color = background, shape = RoundedCornerShape(10.dp))
            .padding(horizontal = 10.dp, vertical = 6.dp)
    ) {
        Text(
            text = "M ${magnitude ?: "-"}",
            style = MaterialTheme.typography.labelLarge,
            color = content
        )
    }
}

private fun magnitudeColors(value: Double?): Pair<Color, Color> = when {
    value == null || value < 4.0 -> MagnitudeCalm to MagnitudeCalmOn
    value < 5.0 -> MagnitudeModerate to MagnitudeModerateOn
    value < 6.0 -> MagnitudeStrong to MagnitudeStrongOn
    else -> MagnitudeSevere to MagnitudeSevereOn
}

/** State: sedang memuat data dari API. */
@Composable
private fun LoadingContent(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Memuat data gempa...",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

/** State: gagal memuat data (jaringan mati / server bermasalah). */
@Composable
private fun ErrorContent(
    message: String,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Default.Warning,
            contentDescription = null,
            modifier = Modifier.size(44.dp),
            tint = MaterialTheme.colorScheme.error
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = message,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurface,
            textAlign = androidx.compose.ui.text.style.TextAlign.Center
        )
        Spacer(modifier = Modifier.height(20.dp))
        Button(
            onClick = onRetry,
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            )
        ) {
            Icon(imageVector = Icons.Default.Refresh, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text(text = "Coba Lagi")
        }
    }
}

/** State: data berhasil dimuat tetapi pencarian tidak menemukan apa pun. */
@Composable
private fun EmptyContent(
    query: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Default.Search,
            contentDescription = null,
            modifier = Modifier.size(44.dp),
            tint = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = if (query.isBlank()) {
                "Belum ada data gempa."
            } else {
                "Tidak ada gempa ditemukan untuk \"$query\"."
            },
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = androidx.compose.ui.text.style.TextAlign.Center
        )
    }
}
