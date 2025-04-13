package com.musicplayer.kathavichar.repositories

import SongEntity
import retrofit2.http.GET
import retrofit2.http.Path

interface SongsService {
    @GET("songs/{artistId}")
    suspend fun getSongs(@Path("artistId") artistId: String): SongEntity
}
