package com.example.kathavichar.repositories.musicPlayer
import android.app.PendingIntent
import android.content.Intent
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.media.app.NotificationCompat
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.session.MediaSession
import androidx.media3.session.MediaSessionService
import com.example.kathavichar.common.Constants
import org.koin.android.ext.android.inject

class MediaService : MediaSessionService() {
    val mediaSession: MediaSession by inject()

    val exoPlayer: ExoPlayer by inject()

    val musicPlayerKathaVichar: MusicPlayerKathaVichar by inject()

    @RequiresApi(Build.VERSION_CODES.O)
    @UnstableApi
    override fun onStartCommand(
        intent: Intent?,
        flags: Int,
        startId: Int,
    ): Int {
        super.onStartCommand(intent, flags, startId)
        musicPlayerKathaVichar.startMusicNotificationService(this, mediaSession)
        return START_STICKY
    }
     override fun onGetSession(controllerInfo: MediaSession.ControllerInfo): MediaSession = mediaSession

    override fun onDestroy() {
        super.onDestroy()
        mediaSession.apply {
            release()
            if (exoPlayer.playbackState != Player.STATE_IDLE) {
                println("fgsfdgads")
                exoPlayer.seekTo(0)
                exoPlayer.playWhenReady = false
                exoPlayer.stop() }
        }
    }
}
