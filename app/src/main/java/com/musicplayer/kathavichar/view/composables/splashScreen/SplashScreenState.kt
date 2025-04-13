package com.musicplayer.kathavichar.view.composables.splashScreen

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.navigation.NavHostController
import com.musicplayer.kathavichar.network.ServerResponse
import com.musicplayer.kathavichar.view.composables.SplashScreen
import com.musicplayer.kathavichar.viewModel.SplashScreenViewModel

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
        is ServerResponse.isLoading -> {
            SplashScreen(navigationController, true)
        }
        is ServerResponse.onSuccess -> { SplashScreen(navigationController, false) }
        is ServerResponse.onError -> {
            uiState.message?.let { ForceUpgradeScreen(message = it, upgradeUrl = "") }
        }
    }
}