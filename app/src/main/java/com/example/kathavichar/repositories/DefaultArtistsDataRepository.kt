package com.example.kathavichar.repositories

import com.example.kathavichar.model.ArtistsData
import com.example.kathavichar.model.ArtistsItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class DefaultArtistsDataRepository : ArtistsDataRepository {
    override suspend fun fetchArtists(): List<ArtistsItem> {
        return withContext(Dispatchers.IO) {
            try {
                 RetrofitClient.instance.getArtists().artists
            } catch (e: Exception) {
                emptyList()
            }
        }
    }
}
