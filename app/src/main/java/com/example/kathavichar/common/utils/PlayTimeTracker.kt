package com.example.kathavichar.common.utils

import android.content.Context
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import com.example.kathavichar.common.SharedPrefsManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.java.KoinJavaComponent

class PlaybackTracker(
    private val context: Context,
    private val lifecycleOwner: LifecycleOwner,
    private val onAdTrigger: () -> Unit,
) {
    private val sharedPreferences: SharedPrefsManager by KoinJavaComponent.inject(SharedPrefsManager::class.java)
    private var accumulatedTime: Long = 0L
    private var isTracking = false
    private var timerJob: Job? = null
    init {
        // Restore accumulated time from SharedPreferences
        accumulatedTime = sharedPreferences.getLong("accumulatedTime", 0L)

        // Observe lifecycle events
        lifecycleOwner.lifecycle.addObserver(
            LifecycleEventObserver { _, event ->
                when (event) {
                    Lifecycle.Event.ON_DESTROY -> saveState()
                    Lifecycle.Event.ON_PAUSE -> saveState()
                    Lifecycle.Event.ON_RESUME -> resumeTrackingIfNeeded()
                    else -> {}
                }
            },
        )
    }

    fun startTracking() {
        if (isTracking) return
        isTracking = true
        timerJob = CoroutineScope(Dispatchers.Default).launch {
            while (isActive) {
                delay(1000) // Increment every second
                accumulatedTime += 1000
                println("accumulatedTime $accumulatedTime")
                if (accumulatedTime >= 15 * 60 * 1000) { // 30 minutes
                    withContext(Dispatchers.Main) { onAdTrigger() }
                    resetTimer()
                }
            }
        }
    }

    fun stopTracking() {
        isTracking = false
        timerJob?.cancel()
    }

    fun resetTimer() {
        accumulatedTime = 0L
        saveState()
    }

    private fun saveState() {
        sharedPreferences.saveLong("accumulatedTime", accumulatedTime)
    }

    private fun resumeTrackingIfNeeded() {
        if (isTracking) startTracking()
    }
}
