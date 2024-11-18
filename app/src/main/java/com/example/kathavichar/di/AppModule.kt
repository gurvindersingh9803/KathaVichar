package com.example.kathavichar.di

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.session.MediaSession
import com.example.kathavichar.repositories.HomeCategoriesFirebase
import com.example.kathavichar.repositories.SongsListFirebase
import com.example.kathavichar.repositories.musicPlayer.MediaService
import com.example.kathavichar.repositories.musicPlayer.MusicPlayerKathaVichar
import com.example.kathavichar.view.musicPlayerService.DefaultMusicPlayerService
import com.example.kathavichar.view.musicPlayerService.MusicPlayerService
import com.example.kathavichar.viewModel.MainViewModel
import com.example.kathavichar.viewModel.MusicPlayerViewModel
import com.example.kathavichar.viewModel.SongsViewModel
import com.google.gson.Gson
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

@RequiresApi(Build.VERSION_CODES.O)
val appModule = module {
    single { Gson() }
    single { HomeCategoriesFirebase() }
    single { SongsListFirebase() }
    viewModel { MainViewModel() }
    viewModel { SongsViewModel() }
    viewModel { MusicPlayerViewModel() }
    single<MusicPlayerService> { DefaultMusicPlayerService(androidContext()) }
    single { ExoPlayer.Builder(androidContext()).build() }
    single {
        MusicPlayerKathaVichar(get(), androidContext())
    }
    single { MediaService() }
    factory {
        MediaSession.Builder(get(), get<ExoPlayer>()).build()
    }
}
