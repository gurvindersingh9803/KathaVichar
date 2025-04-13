package com.musicplayer.kathavichar.repositories

import com.musicplayer.kathavichar.model.Songs
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class DefaultSongsDataRepository(private val songsService: SongsService) : SongsDataRepository {
    override suspend fun fetchSongs(artistName: String): List<Songs> {
        return withContext(Dispatchers.IO) {
            try {
                println("artistName $artistName")
                songsService.getSongs(artistName).songs
            } catch (e: Exception) {
                println("artistName exception $e")
                emptyList()
            }
        }
    }
}