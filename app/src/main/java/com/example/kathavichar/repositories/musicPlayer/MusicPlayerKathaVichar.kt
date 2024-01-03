package com.example.kathavichar.repositories.musicPlayer

import android.util.Log
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.media3.common.MediaItem
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class MusicPlayerKathaVichar(private val exoPlayer: ExoPlayer) : Player.Listener {

    /**
     * A state flow that emits the current playback state of the player.
     */

    private val _playerState: MutableStateFlow<MusicPlayerStates> = MutableStateFlow(MusicPlayerStates.STATE_IDLE)
    val playerState = _playerState.asStateFlow()

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
        if (exoPlayer.playbackState == Player.STATE_IDLE) exoPlayer.prepare()
        exoPlayer.playWhenReady = !exoPlayer.playWhenReady
    }

    fun releasePlayer() {
        exoPlayer.release()
    }

    fun seekToPosition(position: Long) {
        exoPlayer.seekTo(position)
    }

    fun setUpTrack(index: Int, isTrackPlay: Boolean) {
        if (exoPlayer.playbackState == Player.STATE_IDLE) exoPlayer.prepare()
        exoPlayer.seekTo(index, 0)
        if (isTrackPlay) exoPlayer.playWhenReady = true
    }

    override fun onPlayerError(error: PlaybackException) {
        super.onPlayerError(error)
        _playerState.tryEmit(MusicPlayerStates.STATE_ERROR)
    }

    override fun onPlayWhenReadyChanged(playWhenReady: Boolean, reason: Int) {
        if (exoPlayer.playbackState == Player.STATE_READY) {
            if (playWhenReady) {
                _playerState.tryEmit(MusicPlayerStates.STATE_PLAYING)
            } else {
                _playerState.tryEmit(MusicPlayerStates.STATE_PAUSE)
            }
        }
    }
    override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) {
        super.onMediaItemTransition(mediaItem, reason)
        if(reason == Player.MEDIA_ITEM_TRANSITION_REASON_AUTO) {
            _playerState.tryEmit(MusicPlayerStates.STATE_NEXT_TRACK)
            _playerState.tryEmit(MusicPlayerStates.STATE_PLAYING)
        }
    }

    override fun onPlaybackStateChanged(playbackState: Int) {
        super.onPlaybackStateChanged(playbackState)
        Log.i("aqqaqqq", playbackState.toString())
        when (playbackState) {
            Player.STATE_IDLE -> {
                _playerState.tryEmit(MusicPlayerStates.STATE_IDLE)
            }
            Player.STATE_BUFFERING -> {
                _playerState.tryEmit(MusicPlayerStates.STATE_BUFFERING)
            }
            Player.STATE_READY -> {
                _playerState.tryEmit(MusicPlayerStates.STATE_READY)
                if (exoPlayer.playWhenReady) {
                    Log.i("aqqaqqqplayWhenReady", playbackState.toString())

                    _playerState.tryEmit(MusicPlayerStates.STATE_PLAYING)
                } else {
                    Log.i("aqqaqqqplayWhenReadyNooo", playbackState.toString())

                    _playerState.tryEmit(MusicPlayerStates.STATE_PAUSE)
                }
            }
            Player.STATE_ENDED -> {
                _playerState.tryEmit(MusicPlayerStates.STATE_END)
            }
        }
    }
}
