package com.example.kathavichar.di

import com.example.kathavichar.repositories.FirebaseTestRepo
import com.google.gson.Gson
import org.koin.dsl.module

val appModule = module {
    single { Gson() }
    single { FirebaseTestRepo() }
}