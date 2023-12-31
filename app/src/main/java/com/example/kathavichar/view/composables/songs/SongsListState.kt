package com.example.kathavichar.view.composables.songs

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import com.example.kathavichar.common.utils.isDataLoading
import com.example.kathavichar.network.ServerResponse
import com.example.kathavichar.viewModel.SongsViewModel

@Composable
fun SongsListState(navigationController: NavHostController, artistName: String?, viewModel: SongsViewModel, modifier: Modifier) {
    LaunchedEffect(Unit) {
        viewModel.getSongs(artistName!!)
    }
    val uiState by viewModel.uiStateSongs.collectAsState()

    when (uiState) {
        is ServerResponse.isLoading -> isDataLoading()
        is ServerResponse.onSuccess -> SongsListUI(uiState.data, viewModel, navigationController, modifier = modifier)
        is ServerResponse.onError -> {}
    }
}