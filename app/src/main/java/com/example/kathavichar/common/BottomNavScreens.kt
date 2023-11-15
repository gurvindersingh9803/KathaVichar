package com.example.kathavichar.common

sealed class Screen(val route: String) {
    object MainPlayList : Screen("MainPlayList")
    object Download : Screen("Download")
    object SongsList : Screen("SongsList")
}
