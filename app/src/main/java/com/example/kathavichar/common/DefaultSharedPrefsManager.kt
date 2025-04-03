package com.example.kathavichar.common

import android.content.Context
import android.content.SharedPreferences
import com.example.kathavichar.model.Songs
import com.google.gson.Gson

class DefaultSharedPrefsManager(context: Context, private val gson: Gson): SharedPrefsManager {

    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences("playback_prefs", Context.MODE_PRIVATE)

    companion object {
        private const val SELECTED_TRACK_KEY = "selected_track"
    }

    override fun savePlaybackState(selectedTrack: Songs) {
        println("drf $selectedTrack")
        val editor = sharedPreferences.edit()
        val json = gson.toJson(selectedTrack)
        editor.putString(SELECTED_TRACK_KEY, json)
        editor.apply()
    }

    override fun restorePlaybackState(): Songs? {
        val json = sharedPreferences.getString(SELECTED_TRACK_KEY, null)
        return json?.let {
            gson.fromJson(it, Songs::class.java)
        }
    }

    override fun clearPlaybackState() {
        sharedPreferences.edit().remove(SELECTED_TRACK_KEY).apply()
    }
}