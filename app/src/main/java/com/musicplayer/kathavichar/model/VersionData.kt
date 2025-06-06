package com.musicplayer.kathavichar.model

data class VersionInfo(
    val latestVersion: String,
    val needsUpgrade: Boolean,
    val forceUpgrade: Boolean,
    val releaseNotes: String
)

