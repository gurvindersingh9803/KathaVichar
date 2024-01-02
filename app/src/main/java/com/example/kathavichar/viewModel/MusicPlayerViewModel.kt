package com.example.kathavichar.viewModel

import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import com.example.kathavichar.view.musicPlayerService.DefaultMusicPlayerService
import com.example.kathavichar.view.musicPlayerService.DefaultSchedulerProvider
import com.example.kathavichar.view.musicPlayerService.MusicPlayerService
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import org.koin.android.ext.android.inject
import org.koin.java.KoinJavaComponent.inject

class MusicPlayerViewModel() : ViewModel() {

    private val musicPlayerService: MusicPlayerService by inject(MusicPlayerService::class.java)
    private val compositeDisposable = CompositeDisposable()
    private val schedulerProvider = DefaultSchedulerProvider()

    fun playSong(soundFileType: Uri) {
        compositeDisposable.add(
            musicPlayerService.playSound(soundFileType)
                .subscribeOn(schedulerProvider.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    Log.i("Media player task completed", "Completed")
                    musicPlayerService.pauseMusicPlayer()
                }, {
                    Log.i("Media player error occurred", it.toString())
                    musicPlayerService.pauseMusicPlayer()
                }),
        )
    }
}
