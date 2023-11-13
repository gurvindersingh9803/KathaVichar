package com.example.kathavichar

import android.app.Application
import com.example.kathavichar.di.appModule
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.fragment.koin.fragmentFactory
import org.koin.core.context.startKoin

class KathaVicharApp : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            fragmentFactory()
            androidContext(this@KathaVicharApp)
            modules(appModule)
        }
    }
}
