package com.example.kathavichar.repositories.musicPlayer

/**
 * Data class that represents the current playback state of a media item.
 *
 * @property currentPlaybackPosition Current position in the media item that's currently playing, in milliseconds.
 * @property currentTrackDuration Duration of the current track that's playing, in milliseconds.
 */
data class PlayerBackState(val currentPlayBackPosition: Long, val currentTrackDuration: Long)
