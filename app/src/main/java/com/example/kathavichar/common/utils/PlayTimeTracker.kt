package com.example.kathavichar.common.utils

class PlayTimeTracker {
    private var totalPlayTime = 0L
    private var lastCheckpoint = 0L
    // private val AD_THRESHOLD = 30 * 60 * 1000L // 30 minutes in milliseconds
    private val AD_THRESHOLD = 1 * 60 * 1000L // 1 minute in milliseconds


    fun updatePlayTime(currentPosition: Long) {
        if (lastCheckpoint == 0L) {
            lastCheckpoint = currentPosition
            return
        }

        val delta = currentPosition - lastCheckpoint
        if (delta > 0) {
            totalPlayTime += delta
            lastCheckpoint = currentPosition
        }
    }

    fun shouldShowAd(): Boolean {
        return totalPlayTime >= AD_THRESHOLD
    }

    fun resetAfterAd() {
        totalPlayTime = 0L
        lastCheckpoint = 0L
    }
}