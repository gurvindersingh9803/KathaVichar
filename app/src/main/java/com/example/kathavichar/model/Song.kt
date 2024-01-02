package com.example.kathavichar.model

import com.example.kathavichar.repositories.musicPlayer.MusicPlayerStates

data class Song(
    val audioUrl: String,
    val imgUrl: String?,
    val title: String?,
    var state: MusicPlayerStates = MusicPlayerStates.STATE_IDLE
)