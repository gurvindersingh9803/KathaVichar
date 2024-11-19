package com.example.kathavichar.view.composables.songs.main

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ModalBottomSheetLayout
import androidx.compose.material.ModalBottomSheetState
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material.rememberModalBottomSheetState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
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

    val scope = rememberCoroutineScope()
    val onBottomTabClick: () -> Unit = { scope.launch { fullScreenState.show() } }

    Content(
        songsViewModel = songsViewModel,
        fullScreenState,
        onBottomTabClick,
    )
}

@OptIn(ExperimentalMaterialApi::class, ExperimentalMaterial3Api::class)
@Composable
fun Content(
    songsViewModel: SongsViewModel,
    fullScreenState: ModalBottomSheetState,
    onBottomTabClick: () -> Unit,
) {
    ModalBottomSheetLayout(
        sheetContent = {
            if (songsViewModel.selectedTrack != null) {
                BottomSheetDialog(songsViewModel.selectedTrack!!, songsViewModel, songsViewModel.playerStates)
            }
        },
        sheetState = fullScreenState,
        sheetShape = RoundedCornerShape(topStart = 10.dp, topEnd = 10.dp),
        sheetElevation = 12.dp,
    ) {
        Scaffold(topBar = {
        }) { paddingValues ->
            Box(
                modifier =
                    Modifier
                        .padding(top = paddingValues.calculateTopPadding())
                        .fillMaxSize(),
            ) {
                Column(
                    modifier =
                        Modifier
                            .fillMaxSize()
                            .clip(RoundedCornerShape(12.dp)),
                    verticalArrangement = Arrangement.SpaceBetween,
                ) {
                    SongsListUI(songsViewModel)
                    AnimatedVisibility(
                        visible = songsViewModel.selectedTrack != null,
                        enter = slideInVertically(initialOffsetY = { fullHeight -> fullHeight }),
                    ) {
                        BottomPlayerTab(
                            song = songsViewModel.selectedTrack!!,
                            songsViewModel,
                            onBottomTabClick,
                        )
                    }
                }
            }
        }
    }
}
