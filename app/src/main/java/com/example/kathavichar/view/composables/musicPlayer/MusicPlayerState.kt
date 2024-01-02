package com.example.kathavichar.view.composables.musicPlayer

import android.net.Uri
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import com.example.kathavichar.common.sharedComposables.PausePlayIcon
import com.example.kathavichar.model.Song
import com.example.kathavichar.view.musicPlayerService.MusicPlayerService
import com.example.kathavichar.viewModel.MusicPlayerViewModel
import com.google.gson.Gson

@Composable
fun MusicPlayerState(
    musicPlayerViewModel: MusicPlayerViewModel,
    musicPlayerService: MusicPlayerService,
    song: String?,
) {
    LaunchedEffect(Unit) {
       /* val mediaItem = MediaItem.fromUri(Uri.parse(audioUrl))
        musicPlayer.addMediaItem(mediaItem)
        musicPlayer.prepare()
        musicPlayer.play()*/
    }

    Column {
        val gson = Gson()
        val songObj = gson.fromJson(song, Song::class.java)
        // PausePlayIcon(currentSong = songObj, onClick = { musicPlayerViewModel.onPlayPauseClicked()}, isBottomTab = false)
    }
}
