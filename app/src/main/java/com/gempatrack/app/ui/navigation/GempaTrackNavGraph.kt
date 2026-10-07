package com.gempatrack.app.ui.navigation

import android.net.Uri
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.compose.runtime.collectAsState
import com.gempatrack.app.data.remote.model.Gempa
import com.gempatrack.app.ui.detail.DetailScreen
import com.gempatrack.app.ui.home.HomeScreen
import com.gempatrack.app.ui.home.HomeViewModel
import com.google.gson.Gson

private const val ARG_GEMPA_JSON = "gempaJson"

/** Route aplikasi — maksimal 2 screen. */
sealed class GempaTrackRoute(val route: String) {
    data object Home : GempaTrackRoute("home")
    data object Detail : GempaTrackRoute("detail/{$ARG_GEMPA_JSON}")
}

/** Membangun route detail dari objek gempa (JSON di-encode untuk URL). */
fun gempaDetailRoute(gempa: Gempa): String {
    val json = Gson().toJson(gempa)
    return "detail/${Uri.encode(json)}"
}

/**
 * NavHost navigation-compose yang menghubungkan Home dan Detail.
 * ViewModel di-scope di sini sehingga state pencarian tetap hidup
 * saat berpindah layar.
 */
@Composable
fun GempaTrackNavGraph(
    viewModel: HomeViewModel = viewModel(factory = HomeViewModel.Factory)
) {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = GempaTrackRoute.Home.route
    ) {
        composable(GempaTrackRoute.Home.route) {
            val uiState by viewModel.uiState.collectAsState()
            val searchQuery by viewModel.searchQuery.collectAsState()
            val filteredGempa by viewModel.filteredGempa.collectAsState()

            HomeScreen(
                uiState = uiState,
                searchQuery = searchQuery,
                gempaList = filteredGempa,
                onSearchQueryChange = viewModel::onSearchQueryChange,
                onRetry = viewModel::fetchGempaterkini,
                onGempaClick = { gempa ->
                    navController.navigate(gempaDetailRoute(gempa))
                }
            )
        }

        composable(
            route = GempaTrackRoute.Detail.route,
            arguments = listOf(navArgument(ARG_GEMPA_JSON) { type = NavType.StringType })
        ) { entry ->
            val json = entry.arguments?.getString(ARG_GEMPA_JSON).orEmpty()
            val gempa = remember(json) {
                runCatching { Gson().fromJson(json, Gempa::class.java) }.getOrNull()
            }
            DetailScreen(
                gempa = gempa,
                onBack = { navController.popBackStack() }
            )
        }
    }
}
