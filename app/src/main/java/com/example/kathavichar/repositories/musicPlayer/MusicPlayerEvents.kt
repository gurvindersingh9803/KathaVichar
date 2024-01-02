package com.example.kathavichar.repositories.musicPlayer

import com.example.kathavichar.model.Song

interface MusicPlayerEvents {

    fun onPlayPauseClicked()

    fun onPreviousClicked()

    fun onNextClicked()

    fun onTrackClicked(song: Song)

    fun onSeekBarPositionChanged(position: Long)
}
