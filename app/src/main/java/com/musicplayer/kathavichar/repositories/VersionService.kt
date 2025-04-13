package com.musicplayer.kathavichar.repositories

import com.musicplayer.kathavichar.model.VersionInfo
import retrofit2.http.GET
import retrofit2.http.Query

interface VersionService {
        @GET("app-version")
        suspend fun getLatestVersion(@Query("currentVersion") currentVersion: String): VersionInfo
}