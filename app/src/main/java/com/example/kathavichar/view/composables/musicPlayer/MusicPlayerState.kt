package com.example.kathavichar.view.composables.musicPlayer

import android.net.Uri
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import com.example.kathavichar.view.musicPlayerService.MusicPlayerService
import com.example.kathavichar.viewModel.MusicPlayerViewModel

@Composable
fun MusicPlayerState(
    musicPlayerViewModel: MusicPlayerViewModel,
    musicPlayerService: MusicPlayerService,
    audioUrl: String?,
) {
    LaunchedEffect(Unit) {
       /* val mediaItem = MediaItem.fromUri(Uri.parse(audioUrl))
        musicPlayer.addMediaItem(mediaItem)
        musicPlayer.prepare()
        musicPlayer.play()*/

        musicPlayerViewModel.playSong(Uri.parse(audioUrl))
    }
}
