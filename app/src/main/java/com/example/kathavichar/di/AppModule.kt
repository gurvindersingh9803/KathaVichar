package com.example.kathavichar.di

import android.annotation.SuppressLint
import android.os.Build
import androidx.annotation.RequiresApi
import com.example.kathavichar.common.AndroidNetworkStatusProvider
import com.example.kathavichar.common.DefaultSharedPrefsManager
import com.example.kathavichar.common.SharedPrefsManager
import com.example.kathavichar.repositories.ArtistsDataRepository
import com.example.kathavichar.repositories.ArtistsService
import com.example.kathavichar.repositories.DefaultArtistsDataRepository
import com.example.kathavichar.repositories.DefaultSongsDataRepository
import com.example.kathavichar.repositories.DefaultVersionRepository
import com.example.kathavichar.repositories.RetrofitClient
import com.example.kathavichar.repositories.SongsDataRepository
import com.example.kathavichar.repositories.SongsService
import com.example.kathavichar.repositories.VersionRepository
import com.example.kathavichar.repositories.VersionService
import com.example.kathavichar.repositories.musicPla.MusicPlayerKathaVichar
import com.example.kathavichar.repositories.musicPlayer.MediaService
import com.example.kathavichar.view.musicPlayerService.DefaultMusicPlayerService
import com.example.kathavichar.view.musicPlayerService.MusicPlayerService
import com.example.kathavichar.viewModel.MainViewModel
import com.example.kathavichar.viewModel.MusicPlayerViewModel
import com.example.kathavichar.viewModel.SongsViewModel
import com.example.kathavichar.viewModel.SplashScreenViewModel
import com.example.myapp.MediaPlayerNotificationManager
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
