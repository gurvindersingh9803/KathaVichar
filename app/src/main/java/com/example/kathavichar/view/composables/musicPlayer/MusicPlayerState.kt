package com.example.kathavichar.view.composables.musicPlayer

import android.net.Uri
import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import com.example.kathavichar.viewModel.MusicPlayerViewModel

@Composable
fun MusicPlayerState(
    musicPlayerViewModel: MusicPlayerViewModel,
    musicPlayer: ExoPlayer,
    audioUrl: String?
) {
    LaunchedEffect(Unit) {
        Log.i("rftgregyrtyg", audioUrl.toString())
        val mediaItem = MediaItem.fromUri(Uri.parse(audioUrl))
        musicPlayer.addMediaItem(mediaItem)
        musicPlayer.prepare()
        musicPlayer.play()
    }
}
