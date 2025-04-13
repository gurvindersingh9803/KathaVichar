package com.musicplayer.kathavichar.repositories

import com.musicplayer.kathavichar.model.VersionInfo

interface VersionRepository {
    suspend fun getVersionInfo(currentAppVersion: String): VersionInfo
}