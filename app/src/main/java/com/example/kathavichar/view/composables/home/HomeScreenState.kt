package com.example.kathavichar.view.composables.home

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.navigation.NavHostController
import com.example.kathavichar.common.utils.isDataLoading
import com.example.kathavichar.network.ServerResponse
import com.example.kathavichar.viewModel.MainViewModel
import com.example.kathavichar.viewModel.SongsViewModel

@Composable
fun HomeScreenState(
    navigationController: NavHostController,
    viewModel: MainViewModel,
    innerPadding: PaddingValues,
    songsViewModel: SongsViewModel
) {
    LaunchedEffect(Unit) {
        viewModel.getCategories()
    }
    val uiState by viewModel.uiState.collectAsState()

    when (uiState) {
        is ServerResponse.isLoading -> { isDataLoading() } // isDataLoading(modifier = Modifier.size(50.dp))
        is ServerResponse.onSuccess -> ArtistSearchScreen(uiState.data, navigationController, innerPadding, viewModel, songsViewModel)
        is ServerResponse.onError -> {}
    }
}
