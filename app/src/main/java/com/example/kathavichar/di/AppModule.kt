package com.example.kathavichar.di

import com.example.kathavichar.model.Song
import com.example.kathavichar.repositories.HomeCategoriesFirebase
import com.example.kathavichar.repositories.SongsListFirebase
import com.example.kathavichar.viewModel.MainViewModel
import com.google.gson.Gson
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val appModule = module {
    single { Gson() }
    single { HomeCategoriesFirebase() }
    single { SongsListFirebase() }
    viewModel { MainViewModel() }

}