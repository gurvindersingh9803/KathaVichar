package com.example.kathavichar.common

import android.content.Context
import android.content.SharedPreferences
import com.example.kathavichar.model.Songs
import com.google.gson.Gson

class DefaultSharedPrefsManager(context: Context, private val gson: Gson): SharedPrefsManager {

    private val prefs: SharedPreferences =
        context.getSharedPreferences("MusicPlayerPrefs", Context.MODE_PRIVATE)

    override fun saveString(key: String, value: String) {
        prefs.edit().putString(key, value).apply()
    }

    override fun getString(key: String, defaultValue: String?): String? {
        return prefs.getString(key, defaultValue)
    }

    override fun saveLong(key: String, value: Long) {
        prefs.edit().putLong(key, value).apply()
    }

    override fun getLong(key: String, defaultValue: Long): Long {
        return prefs.getLong(key, defaultValue)
    }

    override fun saveInt(key: String, value: Int) {
        prefs.edit().putInt(key, value).apply()
    }

    override fun getInt(key: String, defaultValue: Int): Int {
        return prefs.getInt(key, defaultValue)
    }

    override fun saveBoolean(key: String, value: Boolean) {
        prefs.edit().putBoolean(key, value).apply()
    }

    override fun getBoolean(key: String, defaultValue: Boolean): Boolean {
        return prefs.getBoolean(key, defaultValue)
    }

    override fun clear() {
        prefs.edit().clear().apply()
    }
}