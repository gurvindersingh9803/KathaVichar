package com.example.kathavichar.repositories

import com.example.kathavichar.model.VersionInfo
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface VersionService {
        @GET("app-version")
        suspend fun getLatestVersion(@Query("currentVersion") currentVersion: String): VersionInfo
}