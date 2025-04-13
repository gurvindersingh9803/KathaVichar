package com.musicplayer.kathavichar.repositories

import com.musicplayer.kathavichar.model.ArtistsData
import retrofit2.http.GET

interface ArtistsService {
    @GET("artists")
    suspend fun getArtists(): ArtistsData
}