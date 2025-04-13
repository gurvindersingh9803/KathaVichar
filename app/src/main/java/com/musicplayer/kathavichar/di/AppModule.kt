package com.musicplayer.kathavichar.di

import android.annotation.SuppressLint
import android.os.Build
import androidx.annotation.RequiresApi
import com.musicplayer.kathavichar.common.AndroidNetworkStatusProvider
import com.musicplayer.kathavichar.common.DefaultSharedPrefsManager
import com.musicplayer.kathavichar.common.SharedPrefsManager
import com.musicplayer.kathavichar.repositories.ArtistsDataRepository
import com.musicplayer.kathavichar.repositories.ArtistsService
import com.musicplayer.kathavichar.repositories.DefaultArtistsDataRepository
import com.musicplayer.kathavichar.repositories.DefaultSongsDataRepository
import com.musicplayer.kathavichar.repositories.DefaultVersionRepository
import com.musicplayer.kathavichar.repositories.RetrofitClient
import com.musicplayer.kathavichar.repositories.SongsDataRepository
import com.musicplayer.kathavichar.repositories.SongsService
import com.musicplayer.kathavichar.repositories.VersionRepository
import com.musicplayer.kathavichar.repositories.VersionService
import com.musicplayer.kathavichar.repositories.musicPla.MusicPlayerKathaVichar
import com.musicplayer.kathavichar.repositories.musicPlayer.MediaService
import com.musicplayer.kathavichar.view.composables.musicPlayer.AdManager
import com.musicplayer.kathavichar.view.musicPlayerService.DefaultMusicPlayerService
import com.musicplayer.kathavichar.view.musicPlayerService.MusicPlayerService
import com.musicplayer.kathavichar.viewModel.MainViewModel
import com.musicplayer.kathavichar.viewModel.MusicPlayerViewModel
import com.musicplayer.kathavichar.viewModel.SongsViewModel
import com.musicplayer.kathavichar.viewModel.SplashScreenViewModel
import com.musicplayer.myapp.MediaPlayerNotificationManager
import com.google.gson.Gson
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

@SuppressLint("UnsafeOptInUsageError")
@RequiresApi(Build.VERSION_CODES.O)
val appModule =
    module {
        single { Gson() }
        viewModel { MainViewModel() }
        viewModel { SongsViewModel(get()) }
        viewModel { SplashScreenViewModel() }
        viewModel { MusicPlayerViewModel() }
        single { AdManager(get()) }
        single<SharedPrefsManager> { DefaultSharedPrefsManager(androidContext(), get()) }
        single<ArtistsDataRepository> { DefaultArtistsDataRepository(get()) }
        single<SongsDataRepository> { DefaultSongsDataRepository(get()) }
        single<VersionRepository> { DefaultVersionRepository(get()) }
        single<MusicPlayerService> { DefaultMusicPlayerService(androidContext()) }
        single<MusicPlayerService> { DefaultMusicPlayerService(androidContext()) }
        single { AndroidNetworkStatusProvider(androidContext()) }
        single {
            MusicPlayerKathaVichar(androidContext())
        }
        single { MediaService() }
        single {
            RetrofitClient().provideRetrofit().create(ArtistsService::class.java)
        }
        single {
            RetrofitClient().provideRetrofit().create(SongsService::class.java)
        }
        single {
            RetrofitClient().provideRetrofit().create(VersionService::class.java)
        }
        single { MediaPlayerNotificationManager(androidContext()) }
    }
