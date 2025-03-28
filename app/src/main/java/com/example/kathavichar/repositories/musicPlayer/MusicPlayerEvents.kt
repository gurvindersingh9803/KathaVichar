package com.example.kathavichar.repositories.musicPlayer

import com.example.kathavichar.model.Songs

interface MusicPlayerEvents {

    fun onPlayPauseClicked()

    fun onPreviousClicked(isBottomClick: Boolean = false, song: Songs? = null)

    fun onNextClicked(isBottomClick: Boolean = false, song: Songs? = null)

    fun onTrackClicked(song: Songs)

    fun onSeekBarPositionChanged(position: Long)
}
