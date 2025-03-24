package com.example.kathavichar.repositories

import com.example.kathavichar.model.Songs

interface SongsDataRepository {
    suspend fun fetchSongs(artistName: String): List<Songs>
}
