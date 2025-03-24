package com.example.kathavichar.repositories

import com.example.kathavichar.model.ArtistsItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class DefaultArtistsDataRepository(private val artistsService: ArtistsService) : ArtistsDataRepository {
    override suspend fun fetchArtists(): List<ArtistsItem> {
        return withContext(Dispatchers.IO) {
            try {
                artistsService.getArtists().artists
            } catch (e: Exception) {
                emptyList()
            }
        }
    }
}
