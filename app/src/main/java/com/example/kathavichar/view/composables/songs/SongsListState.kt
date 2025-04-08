package com.example.kathavichar.view.composables.songs

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.example.kathavichar.common.utils.isDataLoading
import com.example.kathavichar.network.ServerResponse
import com.example.kathavichar.view.composables.songs.main.SongScreenParent
import com.example.kathavichar.viewModel.SongsViewModel

@Composable
fun SongsListState(
    artistName: String?,
    artistId: String?,
    viewModel: SongsViewModel,
) {
    LaunchedEffect(Unit) {
        viewModel.getAllSongs(artistId.toString())
    }
    val uiState by viewModel.uiStateSongs.collectAsState()

    when (uiState) {
        is ServerResponse.isLoading -> { isDataLoading() }
        is ServerResponse.onSuccess -> {
            SongScreenParent(songsViewModel = viewModel, artistName, artistId)
        }
        is ServerResponse.onError -> {}
    }
}
