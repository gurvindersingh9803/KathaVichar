package com.musicplayer.kathavichar.common

interface SharedPrefsManager {
    fun saveString(key: String, value: String)
    fun getString(key: String, defaultValue: String? = null): String?

    fun saveLong(key: String, value: Long)
    fun getLong(key: String, defaultValue: Long = 0L): Long

    fun saveInt(key: String, value: Int)
    fun getInt(key: String, defaultValue: Int = 0): Int

    fun saveBoolean(key: String, value: Boolean)
    fun getBoolean(key: String, defaultValue: Boolean = false): Boolean

    fun clear()
}