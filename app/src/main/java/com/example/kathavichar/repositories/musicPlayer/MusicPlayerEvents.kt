package com.example.kathavichar.repositories.musicPlayer

interface MusicPlayerEvents {

    fun onPlayPauseClicked()

    fun onPreviousClicked()

    fun onNextClicked()

    fun onTrackClicked()

    fun onSeekBarPositionChanged(position: Long)
}
