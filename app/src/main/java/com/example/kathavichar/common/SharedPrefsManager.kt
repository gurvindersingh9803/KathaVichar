package com.example.kathavichar.common

import com.example.kathavichar.model.Songs

interface SharedPrefsManager {
    fun savePlaybackState(selectedTrack: Songs)
    fun restorePlaybackState(): Songs?
    fun clearPlaybackState()
}