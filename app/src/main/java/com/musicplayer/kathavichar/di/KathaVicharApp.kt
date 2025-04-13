package com.musicplayer.kathavichar.di

import android.app.Application
import com.google.android.gms.ads.MobileAds
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.fragment.koin.fragmentFactory
import org.koin.core.context.startKoin

class KathaVicharApp : Application() {
    override fun onCreate() {
        super.onCreate()
        MobileAds.initialize(this)
        startKoin {
            fragmentFactory()
            androidContext(this@KathaVicharApp)
            modules(appModule)
        }
    }
}
