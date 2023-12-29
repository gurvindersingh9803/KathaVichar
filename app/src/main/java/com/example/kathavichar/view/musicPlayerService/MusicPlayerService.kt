package com.example.kathavichar.view.musicPlayerService

import android.net.Uri
import io.reactivex.Completable

interface MusicPlayerService {

    fun playSound(soundFile: Uri): Completable
    fun pauseMusicPlayer()
}
