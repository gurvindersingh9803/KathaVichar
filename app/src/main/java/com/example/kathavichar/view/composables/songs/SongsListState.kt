package com.example.kathavichar.view.composables.songs

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.example.kathavichar.network.ServerResponse
import com.example.kathavichar.view.composables.songs.main.SongScreenParent
import com.example.kathavichar.viewModel.SongsViewModel

@Composable
fun SongsListState(
    artistName: String?,
    viewModel: SongsViewModel,
) {
    LaunchedEffect(Unit) {
        viewModel.getAllSongs("cb1861ef-1ff4-44df-93f9-ff9df08552e7")
    }
    val uiState by viewModel.uiStateSongs.collectAsState()

    when (uiState) {
        is ServerResponse.isLoading -> {} // isDataLoading(modifier = Modifier.size(50.dp))
        is ServerResponse.onSuccess -> {
            SongScreenParent(songsViewModel = viewModel)
        }
        is ServerResponse.onError -> {}
    }
}
