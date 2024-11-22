package com.example.kathavichar.repositories.musicPlayer
import android.content.Intent
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.session.MediaSession
import androidx.media3.session.MediaSessionService
import org.koin.android.ext.android.inject

class MediaService : MediaSessionService() {
    val mediaSession: MediaSession by inject()

    val musicPlayerKathaVichar: MusicPlayerKathaVichar by inject()

    @RequiresApi(Build.VERSION_CODES.O)
    @UnstableApi
    override fun onStartCommand(
        intent: Intent?,
        flags: Int,
        startId: Int,
    ): Int {
        musicPlayerKathaVichar.startMusicNotificationService(this, mediaSession)
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onGetSession(controllerInfo: MediaSession.ControllerInfo): MediaSession = mediaSession

    override fun onDestroy() {
        super.onDestroy()
        mediaSession.apply {
            release()
            if (player.playbackState != Player.STATE_IDLE) {
                player.seekTo(0)
                player.playWhenReady = false
                player.stop()
            }
        }
    }
}
