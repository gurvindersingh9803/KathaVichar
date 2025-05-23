package com.musicplayer.kathavichar.view.composables.musicPlayer

import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import com.musicplayer.kathavichar.model.Songs
import com.musicplayer.kathavichar.view.musicPlayerService.MusicPlayerService
import com.musicplayer.kathavichar.viewModel.MusicPlayerViewModel
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
        val songObj = gson.fromJson(song, Songs::class.java)
        // PausePlayIcon(currentSong = songObj, onClick = { musicPlayerViewModel.onPlayPauseClicked()}, isBottomTab = false)
    }
}
