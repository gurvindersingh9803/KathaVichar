package com.example.kathavichar.repositories.musicPlayer

import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class MusicPlayerKathaVichar(private val exoPlayer: ExoPlayer) : Player.Listener {

    /**
     * A state flow that emits the current playback state of the player.
     */

    private val _playerState: MutableStateFlow<MusicPlayerStates> = MutableStateFlow(MusicPlayerStates.STATE_IDLE)
    val playerState = _playerState.asStateFlow()
}
