package com.example.kathavichar.view.composables.songs.main

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import com.example.kathavichar.view.composables.musicPlayer.BottomPlayerTab
import com.example.kathavichar.view.composables.songs.SongsListState
import com.example.kathavichar.viewModel.SongsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SongScreenParent(
    navigationController: NavHostController,
    artistName: String?,
    songsViewModel: SongsViewModel,
) {
    Scaffold() { paddingValues ->
        Box(modifier = Modifier.padding(top = paddingValues.calculateTopPadding())) {
            SongsListState(navigationController = navigationController, artistName = artistName, viewModel = songsViewModel)
            AnimatedVisibility(
                visible = true,
                enter = slideInVertically(initialOffsetY = { fullHeight -> fullHeight }),
            ) {
                songsViewModel.selectedTrack?.let { BottomPlayerTab(song = it, songsViewModel, {}) }
            }
        }
    }
}
