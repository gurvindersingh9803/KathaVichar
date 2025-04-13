package com.musicplayer.kathavichar.repositories

import android.util.Log
import com.musicplayer.kathavichar.model.VersionInfo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class DefaultVersionRepository(private val versionService: VersionService): VersionRepository {
    override suspend fun getVersionInfo(currentAppVersion: String): VersionInfo {
        // Step 1: Validate Version
        if (!isValidVersion(currentAppVersion)) {
            return VersionInfo("", false, false, "Invalid version format.")
        }

        // Step 2: Try fetching the latest version
        return try {
            withContext(Dispatchers.IO) {
                versionService.getLatestVersion(currentAppVersion)
            }
        } catch (e: Exception) {
            // Log the exception for debugging purposes (or use a logging framework)
            Log.e("VersionRepository", "Failed to fetch version info", e)
            VersionInfo("", false, false, "Failed to fetch version info: ${e.localizedMessage}")
        }
    }

    // Helper function to validate the version format
    private fun isValidVersion(version: String): Boolean {
        val semverRegex = """^(\d+\.)?(\d+\.)?(\*|\d+)$""".toRegex()
        return semverRegex.matches(version)
    }
}