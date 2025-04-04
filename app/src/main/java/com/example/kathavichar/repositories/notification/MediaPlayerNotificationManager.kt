package com.example.myapp

import android.app.Notification
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.core.app.NotificationCompat
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.session.MediaSession
import androidx.media3.session.MediaSessionService
import androidx.media3.ui.PlayerNotificationManager
import com.example.kathavichar.R
import com.example.kathavichar.common.Constants
import com.example.kathavichar.repositories.musicPla.MusicPlayerKathaVichar
import org.koin.android.ext.android.inject

@UnstableApi
class MediaPlayerNotificationManager(private val context: Context) : MediaSessionService() {

    private val mediaSession: MediaSession by inject()
    private val exoPlayer: ExoPlayer by inject()
    private lateinit var playerNotificationManager: PlayerNotificationManager

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        setupPlayerNotification()
        return super.onStartCommand(intent, flags, startId)
    }

    private fun setupPlayerNotification() {
        playerNotificationManager = PlayerNotificationManager
            .Builder(
                context,
                Constants.NOTIFICATION_ID,
                Constants.NOTIFICATION_CHANNEL_ID,
            ).setMediaDescriptionAdapter(
                MusicPlayerKathaVichar.MusicNotificationDescriptorAdapter(
                    context,
                    pendingIntent =
                    PendingIntent.getActivity(
                        context,
                        0,
                        Intent(Intent.ACTION_VIEW, Uri.parse("musify://songslist/Maskeen Ji")),
                        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
                    ),
                ),
            ).setSmallIconResourceId(R.drawable.headset)
            .build()
            .also {
                it.setMediaSessionToken(mediaSession.sessionCompatToken)
                it.setUseFastForwardActionInCompactView(true)
                it.setUseRewindActionInCompactView(true)
                it.setUseNextActionInCompactView(true)
                it.setUsePreviousActionInCompactView(true)
                it.setPriority(NotificationCompat.PRIORITY_DEFAULT)
                it.setPlayer(exoPlayer)
            }
    }


    override fun onGetSession(controllerInfo: MediaSession.ControllerInfo): MediaSession {
        return mediaSession
    }

    companion object {
        const val NOTIFICATION_ID = 1
        const val CHANNEL_ID = "media_player_channel"
    }
}
