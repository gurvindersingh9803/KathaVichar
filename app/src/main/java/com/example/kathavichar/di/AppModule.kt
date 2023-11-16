package com.example.kathavichar.di

import com.example.kathavichar.repositories.FirebaseTestRepo
import com.example.kathavichar.viewModel.MainViewModel
import com.google.gson.Gson
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val appModule = module {
    single { Gson() }
    single { FirebaseTestRepo() }
    viewModel { MainViewModel() }

}