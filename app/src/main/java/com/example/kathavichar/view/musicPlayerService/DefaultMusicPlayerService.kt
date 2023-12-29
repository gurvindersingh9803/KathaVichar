package com.example.kathavichar.view.musicPlayerService

import android.content.Context
import android.media.MediaPlayer
import android.net.Uri
import android.util.Log
import io.reactivex.Completable
import io.reactivex.Scheduler
import io.reactivex.schedulers.Schedulers

class DefaultMusicPlayerService(private val context: Context) : MusicPlayerService {
    private var mediaPlayer: MediaPlayer? = null

    override fun playSound(soundFile: Uri): Completable = Completable.create { emitter ->
        try {
            if (mediaPlayer == null) {
                mediaPlayer = MediaPlayer.create(context, soundFile)
                mediaPlayer.apply {
                    this?.start()
                    mediaPlayer?.setOnCompletionListener {
                        emitter.onComplete()
                    }
                }
            }
        } catch (e: Exception) {
            Log.e("Media player error", e.toString())
            emitter.onError(e)
        }
    }

    override fun pauseMusicPlayer() {
        try {
            if (mediaPlayer?.isPlaying == true || mediaPlayer != null) {
                mediaPlayer.apply {
                    mediaPlayer?.stop()
                    mediaPlayer?.release()
                    mediaPlayer = null
                }
            }
        } catch (e: Exception) {
            Log.e("Media player stopping error", e.toString())
            throw e
        }
    }
}

interface SchedulerProvider {
    fun io(): Scheduler
    fun computation(): Scheduler
}

class DefaultSchedulerProvider : SchedulerProvider {
    override fun io(): Scheduler =
        Schedulers.io()

    override fun computation(): Scheduler =
        Schedulers.computation()
}
