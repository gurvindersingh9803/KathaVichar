package com.example.kathavichar.repositories.musicPlayer

import com.example.kathavichar.model.Song

interface MusicPlayerEvents {

    fun onPlayPauseClicked()

    fun onPreviousClicked(isBottomClick: Boolean = false, song: Song? = null)

    fun onNextClicked(isBottomClick: Boolean = false, song: Song? = null)

    fun onTrackClicked(song: Song)

    fun onSeekBarPositionChanged(position: Long)
}
