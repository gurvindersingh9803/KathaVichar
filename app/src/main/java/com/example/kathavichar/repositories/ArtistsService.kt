package com.example.kathavichar.repositories

import com.example.kathavichar.model.ArtistsData
import retrofit2.http.GET

interface ArtistsService {
    @GET("/artists")
    suspend fun getArtists(): ArtistsData
}