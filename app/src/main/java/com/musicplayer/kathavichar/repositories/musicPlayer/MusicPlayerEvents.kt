package com.musicplayer.kathavichar.repositories.musicPlayer

import com.musicplayer.kathavichar.model.Songs

interface MusicPlayerEvents {

    fun onPlayPauseClicked(song: Songs? = null)

    fun onPreviousClicked(isBottomClick: Boolean = false, song: Songs? = null)

    fun onNextClicked(isBottomClick: Boolean = false, song: Songs? = null)

    fun onTrackClicked(song: Songs)

    fun onSeekBarPositionChanged(position: Long)
}
