package com.musicplayer.kathavichar.repositories

import com.musicplayer.kathavichar.model.Songs

interface SongsDataRepository {
    suspend fun fetchSongs(artistName: String): List<Songs>
}
