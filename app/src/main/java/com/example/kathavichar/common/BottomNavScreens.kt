package com.example.kathavichar.common

sealed class Screen(val route: String) {

    object SplashScreen : Screen("SplashScreen")
    object MainPlayList : Screen("MainPlayList")
    object Download : Screen("Download")
    object SongsList : Screen("songslist")
    object MusicPlayerState : Screen("MusicPlayerState")
}
