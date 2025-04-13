package com.musicplayer.kathavichar.repositories

import com.musicplayer.kathavichar.model.ArtistsItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class DefaultArtistsDataRepository(private val artistsService: ArtistsService) : ArtistsDataRepository {
    override suspend fun fetchArtists(): List<ArtistsItem> {
        return withContext(Dispatchers.Main) {
            try {
                artistsService.getArtists().artists
             } catch (e: Exception) {
                emptyList()
            }
        }
    }
}
