package com.example.kathavichar.view.composables.songs.main

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.kathavichar.view.composables.musicPlayer.BottomPlayerTab
import com.example.kathavichar.view.composables.songs.SongsListUI
import com.example.kathavichar.viewModel.SongsViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterialApi::class)
@Composable
fun SongScreenParent(
    navigationController: NavHostController,
    songsViewModel: SongsViewModel,
) {
    val fullScreenState =
        rememberModalBottomSheetState(
            initialValue = ModalBottomSheetValue.Hidden,
            skipHalfExpanded = true,
        )

    val isMusicPlaying = remember { mutableStateOf(false) }

    val scope = rememberCoroutineScope()
    val onBottomTabClick: () -> Unit = { scope.launch { fullScreenState.show() } }

    Scaffold { paddingValues ->
        Row(
            Modifier.fillMaxWidth(),
        ) {
            Content(
                songsViewModel = songsViewModel,
                paddingValues,
                fullScreenState,
                Modifier.weight(1f),
                navigationController,
                onBottomTabClick,
            )
        }
    }
}

@OptIn(ExperimentalMaterialApi::class, ExperimentalMaterial3Api::class)
@Composable
fun Content(
    songsViewModel: SongsViewModel,
    paddingValues: PaddingValues,
    fullScreenState: ModalBottomSheetState,
    modifier: Modifier = Modifier,
    navigationController: NavHostController,
    onBottomTabClick: () -> Unit,
) {
    Box(modifier = Modifier.padding(top = paddingValues.calculateTopPadding())) {
        println("swfgfdg ${songsViewModel.selectedTrack}")
        ModalBottomSheetLayout(
            sheetContent = {
            },
            sheetState = fullScreenState,
            sheetShape = RoundedCornerShape(topStart = 10.dp, topEnd = 10.dp),
            sheetElevation = 12.dp,
        ) {
            Scaffold(topBar = {
            }) { paddingValues ->
                Box(modifier = Modifier.padding(top = paddingValues.calculateTopPadding())) {
                    Column {
                        SongsListUI(songsViewModel, navigationController, modifier = modifier)
                        AnimatedVisibility(
                            visible = songsViewModel.selectedTrack != null,
                            enter = slideInVertically(initialOffsetY = { fullHeight -> fullHeight }),
                        ) {
                            songsViewModel.selectedTrack?.let {
                                BottomPlayerTab(
                                    song = it,
                                    songsViewModel,
                                    onBottomTabClick,
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
