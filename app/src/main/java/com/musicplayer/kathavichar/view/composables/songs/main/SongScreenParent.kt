package com.musicplayer.kathavichar.view.composables.songs.main

import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Divider
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ModalBottomSheetLayout
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material.rememberModalBottomSheetState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.musicplayer.kathavichar.view.composables.musicPlayer.BottomPlayerTab
import com.musicplayer.kathavichar.view.composables.musicPlayer.BottomSheetDialog
import com.musicplayer.kathavichar.view.composables.songs.SongsListUI
import com.musicplayer.kathavichar.viewModel.SongsViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterialApi::class, ExperimentalMaterial3Api::class)
@Composable
fun SongScreenParent(songsViewModel: SongsViewModel, artistName: String?, artistId: String?) {
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

    ModalBottomSheetLayout(
        sheetContent = {
            selectedTrack?.let { track ->
                BottomSheetDialog(track, songsViewModel, songsViewModel.playbackState, fullScreenState.isVisible)
            }
        },
        sheetState = fullScreenState,
        sheetShape = RoundedCornerShape(topStart = 10.dp, topEnd = 10.dp),
        sheetElevation = 12.dp,
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Column(
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp),
                        ) {
                            androidx.compose.material.Text(
                                "Songs",
                                style = MaterialTheme.typography.displayLarge.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 32.sp,
                                    color = Color(0xFF909090),
                                ),
                            )
                            Divider(
                                modifier = Modifier
                                    .fillMaxWidth() // Make sure it spans the full width
                                    .height(1.dp), // Increased height to make it visible
                                color = Color.LightGray, // Light color for the divider
                            )
                        }
                    },
                )
            },

            content = { padding ->
                SongsListUI(songsViewModel, padding, artistId, artistName)
            },
            bottomBar = {
                AnimatedVisibility(
                    visible = selectedTrack != null,
                    enter = slideInVertically(initialOffsetY = { fullHeight -> fullHeight }),
                ) {
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
}
