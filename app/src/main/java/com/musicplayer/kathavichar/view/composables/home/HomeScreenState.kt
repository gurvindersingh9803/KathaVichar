package com.musicplayer.kathavichar.view.composables.home

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.navigation.NavHostController
import com.musicplayer.kathavichar.common.utils.isDataLoading
import com.musicplayer.kathavichar.network.ServerResponse
import com.musicplayer.kathavichar.viewModel.MainViewModel
import com.musicplayer.kathavichar.viewModel.SongsViewModel

@Composable
fun HomeScreenState(
    navigationController: NavHostController,
    viewModel: MainViewModel,
    innerPadding: PaddingValues,
    songsViewModel: SongsViewModel
) {
    val isNetworkAvailable by viewModel.isNetworkAvailable.collectAsState()
    val uiState by viewModel.uiState.collectAsState()

    // This ensures that network changes trigger the effect each time
    LaunchedEffect(isNetworkAvailable) {
        println("Network status changed: $isNetworkAvailable")
            viewModel.getCategories() // Refresh data when network is back online

    }

    when (uiState) {
        is ServerResponse.isLoading -> { isDataLoading("Fetching artists...") }
        is ServerResponse.onSuccess -> ArtistSearchScreen(
            uiState.data,
            navigationController,
            innerPadding,
            viewModel,
            songsViewModel,
            null
        )
        is ServerResponse.onError -> { ArtistSearchScreen(null, navigationController, innerPadding, viewModel, songsViewModel, uiState.message) }
    }
}
