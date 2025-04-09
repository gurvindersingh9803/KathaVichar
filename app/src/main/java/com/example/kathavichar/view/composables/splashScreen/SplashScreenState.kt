package com.example.kathavichar.view.composables.splashScreen

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.navigation.NavHostController
import com.example.kathavichar.common.utils.isDataLoading
import com.example.kathavichar.network.ServerResponse
import com.example.kathavichar.view.composables.SplashScreen
import com.example.kathavichar.view.composables.home.ArtistSearchScreen
import com.example.kathavichar.viewModel.MainViewModel
import com.example.kathavichar.viewModel.SongsViewModel
import com.example.kathavichar.viewModel.SplashScreenViewModel
import kotlinx.coroutines.delay

@Composable
fun SplashScreenState(
    navigationController: NavHostController,
    appVersion: String,
    splashScreenViewModel: SplashScreenViewModel
) {
    val uiState by splashScreenViewModel.uiState.collectAsState()

    // This ensures that network changes trigger the effect each time
    LaunchedEffect(Unit) {
        splashScreenViewModel.fetchVersionInfo(appVersion)
    }

    when (uiState) {
        is ServerResponse.isLoading -> SplashScreen(
            navigationController,
        )
        is ServerResponse.onSuccess -> { isDataLoading("Fetching version info...") }
        is ServerResponse.onError -> {  }
    }
}