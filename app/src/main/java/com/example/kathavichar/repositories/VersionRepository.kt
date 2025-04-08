package com.example.kathavichar.repositories

import com.example.kathavichar.model.VersionInfo

interface VersionRepository {
    suspend fun getVersionInfo(currentAppVersion: String): VersionInfo
}