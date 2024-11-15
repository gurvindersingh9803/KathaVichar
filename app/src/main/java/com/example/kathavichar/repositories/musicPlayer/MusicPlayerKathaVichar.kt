package com.example.kathavichar.repositories.musicPlayer

import androidx.media3.common.MediaItem
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.common.Tracks
import androidx.media3.exoplayer.ExoPlayer
import kotlinx.coroutines.flow.MutableStateFlow

class MusicPlayerKathaVichar(
    private val exoPlayer: ExoPlayer,
) : Player.Listener {
    override fun onMediaItemTransition(
        mediaItem: MediaItem?,
        reason: Int,
    ) {
        super.onMediaItemTransition(mediaItem, reason)
    }

    override fun onTracksChanged(tracks: Tracks) {
        super.onTracksChanged(tracks)
    }

    override fun onPlaybackStateChanged(playbackState: Int) {
        when (playbackState) {
            Player.STATE_IDLE -> {
                playerState.tryEmit(MusicPlayerStates.STATE_IDLE)
            }

            Player.STATE_BUFFERING -> {
                playerState.tryEmit(MusicPlayerStates.STATE_BUFFERING)
            }

            Player.STATE_READY -> {
                playerState.tryEmit(MusicPlayerStates.STATE_READY)
                if (exoPlayer.playWhenReady) {
                    playerState.tryEmit(MusicPlayerStates.STATE_PLAYING)
                } else {
                    playerState.tryEmit(MusicPlayerStates.STATE_PAUSE)
                }
            }

            Player.STATE_ENDED -> {
                playerState.tryEmit(MusicPlayerStates.STATE_END)
            }
        }
    }

    override fun onPlayWhenReadyChanged(
        playWhenReady: Boolean,
        reason: Int,
    ) {
        super.onPlayWhenReadyChanged(playWhenReady, reason)
    }

    override fun onPlayerError(error: PlaybackException) {
        super.onPlayerError(error)
    }

    val playerState = MutableStateFlow(MusicPlayerStates.STATE_IDLE)

    // val _playerState = MutableSharedFlow(MusicPlayerStates.STATE_IDLE).asSharedFlow()

    val currentPlaybackPosition: Long
        get() = if (exoPlayer.currentPosition > 0) exoPlayer.currentPosition else 0L

    val currentTrackDuration: Long
        get() = if (exoPlayer.duration > 0) exoPlayer.duration else 0L

    fun initMusicPlayer(songsList: MutableList<MediaItem>) {
        exoPlayer.addListener(this)
        exoPlayer.setMediaItems(songsList)
        exoPlayer.prepare()
    }

    fun playPause() {
        var a = exoPlayer.playbackState == Player.STATE_IDLE
        println("fgbdf $a")
        if (exoPlayer.playbackState == Player.STATE_IDLE) exoPlayer.prepare()
        exoPlayer.playWhenReady = !exoPlayer.playWhenReady
    }

    fun releasePlayer() {
        exoPlayer.release()
    }

    fun seekToPosition(position: Long) {
        exoPlayer.seekTo(position)
    }

    fun setUpTrack(
        index: Int,
        isTrackPlay: Boolean,
    ) {
        println("qrfgegf $isTrackPlay $index ${exoPlayer.playbackState }")
        if (exoPlayer.playbackState == Player.STATE_IDLE) exoPlayer.prepare()
        exoPlayer.seekTo(index, 0)
        if (isTrackPlay) exoPlayer.playWhenReady = true
    }
}
