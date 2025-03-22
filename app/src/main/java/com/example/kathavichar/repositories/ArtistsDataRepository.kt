package com.example.kathavichar.repositories

import com.example.kathavichar.model.ArtistsData
import com.example.kathavichar.model.ArtistsItem

interface ArtistsDataRepository {
    suspend fun fetchArtists(): List<ArtistsItem>
}