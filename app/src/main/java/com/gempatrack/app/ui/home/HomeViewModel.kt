package com.gempatrack.app.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.gempatrack.app.data.remote.model.Gempa
import com.gempatrack.app.data.repository.GempaRepository
import com.gempatrack.app.ui.state.UiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

/**
 * ViewModel Home: memegang UiState hasil fetch + query pencarian.
 * Composable hanya mengumpulkan StateFlow (state-driven UI).
 */
class HomeViewModel(
    private val repository: GempaRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<UiState<List<Gempa>>>(UiState.Loading)
    val uiState: StateFlow<UiState<List<Gempa>>> = _uiState.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    /**
     * Hasil filter lokal berdasarkan Wilayah (case-insensitive, contains).
     * Berjalan di memori — tidak menambah request jaringan.
     */
    val filteredGempa: StateFlow<List<Gempa>> =
        combine(_uiState, _searchQuery) { state, query -> filterByWilayah(state, query) }
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    init {
        fetchGempaterkini()
    }

    fun fetchGempaterkini() {
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            _uiState.value = repository.getGempaterkini().fold(
                onSuccess = { data -> UiState.Success(data) },
                onFailure = { error -> UiState.Error(error.message ?: "Terjadi kesalahan.") }
            )
        }
    }

    fun onSearchQueryChange(query: String) {
        _searchQuery.value = query
    }

    private fun filterByWilayah(state: UiState<List<Gempa>>, query: String): List<Gempa> {
        val data = (state as? UiState.Success)?.data ?: return emptyList()
        if (query.isBlank()) return data
        return data.filter { gempa -> gempa.Wilayah?.contains(query, ignoreCase = true) == true }
    }

    companion object {
        val Factory = viewModelFactory {
            initializer { HomeViewModel(GempaRepository()) }
        }
    }
}
