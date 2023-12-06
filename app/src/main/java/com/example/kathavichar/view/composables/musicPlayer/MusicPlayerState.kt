package com.example.kathavichar.view.composables.musicPlayer

import android.net.Uri
import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import com.example.kathavichar.viewModel.MusicPlayerViewModel

@Composable
fun MusicPlayerState(musicPlayerViewModel: MusicPlayerViewModel, musicPlayer: ExoPlayer) {
    LaunchedEffect(Unit) {
        val mediaItem = MediaItem.fromUri(Uri.parse("https://firebasestorage.googleapis.com/v0/b/kathavichar-a5b4e.appspot.com/o/allKatha%2FSantMaskeenJi%2F20dc0c28-a4b0-45f0-a3c2-fec1548ca542.mp3?alt=media&token=28806dff-4de7-4b47-93f1-dfa960ee0647"))
        musicPlayer.addMediaItem(mediaItem)
        musicPlayer.prepare()
        Log.i("rwfqwetrt", musicPlayer.toString())
        musicPlayer.play()
    }
}
