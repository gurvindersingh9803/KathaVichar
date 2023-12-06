package com.example.kathavichar.viewModel

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer

class MusicPlayerViewModel() : ViewModel() {

    init {
        //exoPlayer.prepare()
    }

    fun startMusicPlayer() {
        val mediaItem = MediaItem.fromUri(Uri.parse("https://firebasestorage.googleapis.com/v0/b/kathavichar-a5b4e.appspot.com/o/allKatha%2FSantMaskeenJi%2F20dc0c28-a4b0-45f0-a3c2-fec1548ca542.mp3?alt=media&token=28806dff-4de7-4b47-93f1-dfa960ee0647"))
        //exoPlayer.addMediaItem(mediaItem)
    }
}
