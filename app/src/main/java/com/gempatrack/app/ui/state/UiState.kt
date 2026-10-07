package com.gempatrack.app.ui.state

/**
 * State UI global (sealed class sesuai pola P5).
 * Loading / Success / Error dirender oleh Composable, bukan oleh logika fetch.
 */
sealed interface UiState<out T> {

    data object Loading : UiState<Nothing>

    data class Success<T>(val data: T) : UiState<T>

    data class Error(val message: String) : UiState<Nothing>
}
