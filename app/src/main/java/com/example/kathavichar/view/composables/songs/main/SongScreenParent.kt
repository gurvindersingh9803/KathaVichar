package com.example.kathavichar.view.composables.songs.main

import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ModalBottomSheetLayout
import androidx.compose.material.ModalBottomSheetState
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material.rememberModalBottomSheetState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.kathavichar.model.Songs
import com.example.kathavichar.view.composables.musicPlayer.BottomPlayerTab
import com.example.kathavichar.view.composables.musicPlayer.BottomSheetDialog
import com.example.kathavichar.view.composables.songs.SongsListUI
import com.example.kathavichar.viewModel.SongsViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterialApi::class, ExperimentalMaterial3Api::class)
@Composable
fun SongScreenParent(songsViewModel: SongsViewModel) {
    val fullScreenState =
        rememberModalBottomSheetState(
            initialValue = ModalBottomSheetValue.Hidden,
            skipHalfExpanded = true,
        )
    val selectedTrack by rememberUpdatedState(songsViewModel.selectedTrack)

    val scope = rememberCoroutineScope()

    val onBottomTabClick: () -> Unit = {
        Log.d("BottomTab", "BottomTab clicked")
        scope.launch {
            if (fullScreenState.isVisible) {
                fullScreenState.hide()
            } else {
                fullScreenState.show()
            }
        }
    }



    Scaffold(
        content = { padding ->
            Content(
                songsViewModel = songsViewModel,
                fullScreenState = fullScreenState,
                innerPadding = padding,
                selectedTrack,
            )
        },
        bottomBar = {
            AnimatedVisibility(
                visible = selectedTrack != null,
                enter = slideInVertically(initialOffsetY = { fullHeight -> fullHeight }),
            ) {
                println("srtdfy $selectedTrack")
                selectedTrack?.let { track ->
                    BottomPlayerTab(
                        song = track,
                        songsViewModel,
                        onBottomTabClick = onBottomTabClick,
                    )
                }
            }
        },
    )
}

fun BottomPlayerTab(song: Songs, viewModel: SongsViewModel, onBottomTabClick: () -> Unit) {
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun Content(
    songsViewModel: SongsViewModel,
    fullScreenState: ModalBottomSheetState,
    innerPadding: PaddingValues,
    selectedTrack: Songs?,
) {
    ModalBottomSheetLayout(
        sheetContent = {
            selectedTrack?.let { track ->
                BottomSheetDialog(track, songsViewModel, songsViewModel.playbackState)
            }
        },
        sheetState = fullScreenState,
        sheetShape = RoundedCornerShape(topStart = 10.dp, topEnd = 10.dp),
        sheetElevation = 12.dp,
    ) {
        SongsListUI(songsViewModel, innerPadding)
    }
}
