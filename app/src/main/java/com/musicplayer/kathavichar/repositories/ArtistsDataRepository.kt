package com.musicplayer.kathavichar.repositories

import com.musicplayer.kathavichar.model.ArtistsItem

interface ArtistsDataRepository {
    suspend fun fetchArtists(): List<ArtistsItem>
}