package com.example.kathavichar.repositories.musicPlayer

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.graphics.Bitmap
import android.os.Build
import androidx.annotation.OptIn
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.common.Tracks
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.session.MediaSession
import androidx.media3.session.MediaSessionService
import androidx.media3.ui.PlayerNotificationManager
import com.example.kathavichar.R
import com.example.kathavichar.common.Constants

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(UnstableApi::class)
class MusicPlayerKathaVichar(
    private val exoPlayer: ExoPlayer,
    private val context: Context,
) : Player.Listener {
    private var isServiceRunning = false

    private val _playerState = MutableLiveData<MusicPlayerStates>()
    val playerStates: LiveData<MusicPlayerStates> get() = _playerState
    protected lateinit var mediaSession: MediaSession

    val currentPlaybackPosition: Long
        get() = if (exoPlayer.currentPosition > 0) exoPlayer.currentPosition else 0L

    val currentTrackDuration: Long
        get() = if (exoPlayer.duration > 0) exoPlayer.duration else 0L

    // protected lateinit var mediaSession: MediaSession

    private var isStarted = false
    private val musicNotificationManager: NotificationManagerCompat =
        NotificationManagerCompat.from(context)

    init {
        createMusicNotificationChannel()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createMusicNotificationChannel() {
        val musicNotificationChannel =
            NotificationChannel(
                Constants.NOTIFICATION_CHANNEL_ID,
                Constants.NOTIFICATION_CHANNEL_NAME,
                NotificationManager.IMPORTANCE_DEFAULT,
            )
        println("nvn $musicNotificationChannel")
        musicNotificationChannel.description = "Music playback controls"

        musicNotificationManager.createNotificationChannel(musicNotificationChannel)
    }

    @UnstableApi
    private fun buildMusicNotification(mediaSession: MediaSession) {
        PlayerNotificationManager
            .Builder(
                context,
                Constants.NOTIFICATION_ID,
                Constants.NOTIFICATION_CHANNEL_ID,
            ).setMediaDescriptionAdapter(
                MusicNotificationDescriptorAdapter(
                    context = context,
                    pendingIntent = mediaSession.sessionActivity,
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

    @RequiresApi(Build.VERSION_CODES.O)
    @UnstableApi
    fun startMusicNotificationService(
        mediaSessionService: MediaSessionService,
        mediaSession: MediaSession,
    ) {
        buildMusicNotification(mediaSession)
        startForegroundMusicService(mediaSessionService)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun startForegroundMusicService(mediaSessionService: MediaSessionService) {
        val musicNotification =
            NotificationCompat
                .Builder(context, Constants.NOTIFICATION_CHANNEL_ID)
                .setContentTitle("Music Player")
                .setContentText("Playing music")
                .setSmallIcon(R.drawable.headset)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .build()

        mediaSessionService.startForeground(Constants.NOTIFICATION_ID, musicNotification)
    }

    @OptIn(UnstableApi::class)
    fun initMusicPlayer(songsList: MutableList<MediaItem>) {
        exoPlayer.addListener(this)
        exoPlayer.setMediaItems(songsList)
        exoPlayer.prepare()
    }

    fun createMediaItem(title: String): MediaItem {
        val mediaMetadata =
            MediaMetadata
                .Builder()
                .setTitle(title)
                .build()

        return MediaItem
            .Builder()
            .setMediaMetadata(mediaMetadata)
            .build()
    }

    fun playPause() {
        if (exoPlayer.playbackState == Player.STATE_IDLE) exoPlayer.prepare()
        exoPlayer.playWhenReady = !exoPlayer.playWhenReady
    }

    fun releasePlayer() {
        exoPlayer.release()
    }

    fun seekToPosition(position: Long) {
        exoPlayer.seekTo(position)
    }

    fun setUpTrack(
        index: Int,
        isTrackPlay: Boolean,
    ) {
        if (exoPlayer.playbackState == Player.STATE_IDLE) exoPlayer.prepare()
        exoPlayer.seekTo(index, 0)
        if (isTrackPlay) exoPlayer.playWhenReady = true
    }

    override fun onMediaItemTransition(
        mediaItem: MediaItem?,
        reason: Int,
    ) {
        super.onMediaItemTransition(mediaItem, reason)
        if (reason == Player.MEDIA_ITEM_TRANSITION_REASON_AUTO) {
            _playerState.postValue(MusicPlayerStates.STATE_NEXT_TRACK)
            // _playerState.postValue(MusicPlayerStates.STATE_PLAYING)
        }
    }

    override fun onTracksChanged(tracks: Tracks) {
        super.onTracksChanged(tracks)
    }

    override fun onPlaybackStateChanged(playbackState: Int) {
        when (playbackState) {
            Player.STATE_IDLE -> {
                _playerState.postValue(MusicPlayerStates.STATE_IDLE)
            }

            Player.STATE_BUFFERING -> {
                _playerState.postValue(MusicPlayerStates.STATE_BUFFERING)
            }

            Player.STATE_READY -> {
                _playerState.postValue(MusicPlayerStates.STATE_READY)

                if (exoPlayer.playWhenReady) {
                    _playerState.postValue(MusicPlayerStates.STATE_PLAYING)
                } else {
                    _playerState.postValue(MusicPlayerStates.STATE_PAUSE)
                }
            }

            Player.STATE_ENDED -> {
                _playerState.postValue(MusicPlayerStates.STATE_END)
            }
        }
    }

    override fun onPlayWhenReadyChanged(
        playWhenReady: Boolean,
        reason: Int,
    ) {
        if (exoPlayer.playbackState == Player.STATE_READY) {
            if (playWhenReady) {
                _playerState.postValue(MusicPlayerStates.STATE_PLAYING)
            } else {
                _playerState.postValue(MusicPlayerStates.STATE_PAUSE)
            }
        }
    }

    override fun onPlayerError(error: PlaybackException) {
        super.onPlayerError(error)
    }
}

@UnstableApi
class MusicNotificationDescriptorAdapter(
    private val context: Context,
    private val pendingIntent: PendingIntent?,
) : PlayerNotificationManager.MediaDescriptionAdapter {
    override fun getCurrentContentTitle(player: Player): CharSequence = player.mediaMetadata.title ?: "Unknown"

    override fun createCurrentContentIntent(player: Player): PendingIntent? = pendingIntent

    override fun getCurrentContentText(player: Player): CharSequence = player.mediaMetadata.displayTitle ?: "Unknown"

    override fun getCurrentLargeIcon(
        player: Player,
        callback: PlayerNotificationManager.BitmapCallback,
    ): Bitmap? {
        println("dffdfef")

        return null
    }
}
