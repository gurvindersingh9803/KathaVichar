package com.example.kathavichar.repositories.musicPlayer

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import androidx.annotation.OptIn
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.media3.common.MediaItem
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
import com.example.kathavichar.view.musicPlayerService.MusicPlayerService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(UnstableApi::class)
class MusicPlayerKathaVichar(
    private val exoPlayer: ExoPlayer,
    private val context: Context,
) : Player.Listener {
    private var isServiceRunning = false
    private lateinit var mediaSession: MediaSession
    private lateinit var mediaService: MediaService

    /*    private val _playerState = MutableLiveData<MusicPlayerStates>()
    val playerStates: LiveData<MusicPlayerStates> get() = _playerState
    protected lateinit var mediaSession: MediaSession

    private val _currentPlayingSongIndex = MutableLiveData<Int?>()*/ //
    // val currentPlayingSongIndex: LiveData<Int?> get() = _currentPlayingSongIndex
/*    private val _playerStates = MutableStateFlow(MusicPlayerState())
    val playerStates: StateFlow<MusicPlayerState> = _playerStates.asStateFlow()*/

    val _playerStates = MutableStateFlow(MusicPlayerStates.STATE_IDLE)


    /* private val _playerState = MutableLiveData<MusicPlayerState>()
    val playerStates: LiveData<MusicPlayerState> get() = _playerState*/
    val currentPlaybackPosition: Long
        get() = if (exoPlayer.currentPosition > 0) exoPlayer.currentPosition else 0L

    val currentTrackDuration: Long
        get() = if (exoPlayer.duration > 0) exoPlayer.duration else 0L

    // protected lateinit var mediaSession: MediaSession

    private var isStarted = false
    private val musicNotificationManager: NotificationManagerCompat =
        NotificationManagerCompat.from(context)


    @OptIn(UnstableApi::class)
    fun initMusicPlayer(songsList: MutableList<MediaItem>) {
        println("fgfrgfg $songsList")
        exoPlayer.addListener(this)
        exoPlayer.setMediaItems(songsList)
        exoPlayer.prepare()


    }

    /*fun setupMediaSession() {
        mediaSession = MediaSession(context, "MusicPlayerKathaVicharSession")
        mediaSession.setCallback(object : MediaSession.Callback() {
            override fun onPlay() {
                super.onPlay()
                exoPlayer.playWhenReady = true
                updateMediaSessionPlaybackState()
            }

            override fun onPause() {
                super.onPause()
                exoPlayer.playWhenReady = false
                updateMediaSessionPlaybackState()
            }

            override fun onSkipToNext() {
                super.onSkipToNext()
                exoPlayer.seekToNextMediaItem()
            }

            override fun onSkipToPrevious() {
                super.onSkipToPrevious()
                exoPlayer.seekToPreviousMediaItem()
            }

            override fun onSeekTo(pos: Long) {
                super.onSeekTo(pos)
                exoPlayer.seekTo(pos)
            }
        })
        mediaSession.isActive = true
    }*/


   /* @UnstableApi
    fun startMusicNotificationService(mediaService: MediaService, mediaSession: MediaSession) {
        val mediaDescriptionAdapter = MusicNotificationDescriptorAdapter(
            context,
            PendingIntent.getActivity(
                context,
                0,
                Intent(Intent.ACTION_VIEW).apply {
                    data = Uri.parse("musify://songsl ist") // Modify the intent as needed
                },
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
        )

        val notificationManager = PlayerNotificationManager
            .Builder(context, Constants.NOTIFICATION_ID, Constants.NOTIFICATION_CHANNEL_ID)
            .setMediaDescriptionAdapter(mediaDescriptionAdapter)
            .setSmallIconResourceId(R.drawable.headset) // Your small icon
            .build()

        notificationManager.setPlayer(exoPlayer)

        // Optionally, you can configure the actions in your notification
        notificationManager.setUseNextActionInCompactView(true)
        notificationManager.setUsePreviousActionInCompactView(true)
    }*/

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
        println("etwdhyrthjuy ${exoPlayer.mediaMetadata.albumArtist}")
        val musicNotification =
            NotificationCompat
                .Builder(context, Constants.NOTIFICATION_CHANNEL_ID)
                .setContentTitle("Music Player")
                .setContentText("Playing music")
                .setSmallIcon(R.drawable.headset)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(
                    PendingIntent.getActivity(
                        context,
                        0,
                        Intent(Intent.ACTION_VIEW, Uri.parse("musify://songslist/Maskeen Ji")),
                        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
                    ),
                ).build()

        mediaSessionService.startForeground(Constants.NOTIFICATION_ID, musicNotification)
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

        if (isTrackPlay) exoPlayer.playWhenReady = true
        println("sadfdsfgsdf $index $isTrackPlay")
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
                _playerStates.tryEmit(
                        MusicPlayerStates.STATE_NEXT_TRACK

                )
            }

    }

    override fun onTracksChanged(tracks: Tracks) {
        super.onTracksChanged(tracks)
    }

    override fun onPlaybackStateChanged(playbackState: Int) {
        println("onPlaybackStateChanged ghhjghj")
            when (playbackState) {
                Player.STATE_IDLE -> {
                        _playerStates.tryEmit(
                                MusicPlayerStates.STATE_IDLE

                        )


                    // _playerState.postValue(MusicPlayerStates.STATE_IDLE)
                }

                Player.STATE_BUFFERING -> {
                        _playerStates.tryEmit(

                                MusicPlayerStates.STATE_BUFFERING

                        )


                    // _playerState.postValue(MusicPlayerStates.STATE_BUFFERING)
                }

                Player.STATE_READY -> {
                        _playerStates.tryEmit(
                                MusicPlayerStates.STATE_READY

                        )


                    // _playerState.postValue(MusicPlayerStates.STATE_READY)

                    if (exoPlayer.playWhenReady) {
                            _playerStates.tryEmit(
                                    MusicPlayerStates.STATE_PLAYING

                            )


                        // _playerState.postValue(MusicPlayerStates.STATE_PLAYING)
                    } else {
                            _playerStates.tryEmit(
                                    MusicPlayerStates.STATE_PAUSE

                            )


                        //  _playerState.postValue(MusicPlayerStates.STATE_PAUSE)
                    }
                }

                Player.STATE_ENDED -> {
                        _playerStates.tryEmit(
                                MusicPlayerStates.STATE_END

                        )


                    // _playerState.postValue(MusicPlayerStates.STATE_END)
                }
            }

    }

    override fun onPlayWhenReadyChanged(
        playWhenReady: Boolean,
        reason: Int,
    ) {
        println("onPlayWhenReadyChanged ghhjghj")
            if (exoPlayer.playbackState == Player.STATE_READY) {
                if (playWhenReady) {
                    _playerStates.tryEmit(
                            MusicPlayerStates.STATE_PLAYING

                    )

                    //  _playerState.postValue(MusicPlayerStates.STATE_PLAYING)
                } else {
                    _playerStates.tryEmit(
                            MusicPlayerStates.STATE_PAUSE

                    )

                    //  _playerState.postValue(MusicPlayerStates.STATE_PAUSE)
                }

        }
    }

    override fun onPlayerError(error: PlaybackException) {
        super.onPlayerError(error)
    }

    /*    @RequiresApi(Build.VERSION_CODES.O)
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
        println("etwdhyrthjuy ${exoPlayer.mediaMetadata.albumArtist}")
        val musicNotification =
            NotificationCompat
                .Builder(context, Constants.NOTIFICATION_CHANNEL_ID)
                .setContentTitle("Music Player")
                .setContentText("Playing music")
                .setSmallIcon(R.drawable.headset)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(
                    PendingIntent.getActivity(
                        context,
                        0,
                        Intent(Intent.ACTION_VIEW, Uri.parse("musify://songslist/Maskeen Ji")),
                        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
                    ),
                ).build()

        mediaSessionService.startForeground(Constants.NOTIFICATION_ID, musicNotification)
    }
}*/

    @UnstableApi
    class MusicNotificationDescriptorAdapter(
        private val context: Context,
        private val pendingIntent: PendingIntent?,
    ) : PlayerNotificationManager.MediaDescriptionAdapter {
        override fun getCurrentContentTitle(player: Player): CharSequence =
            player.mediaMetadata.title ?: "Unknown"

        override fun createCurrentContentIntent(player: Player): PendingIntent? = pendingIntent

        override fun getCurrentContentText(player: Player): CharSequence =
            player.mediaMetadata.displayTitle ?: "Unknown"

        override fun getCurrentLargeIcon(
            player: Player,
            callback: PlayerNotificationManager.BitmapCallback,
        ): Bitmap? {
            println("dffdfef")

            return null
        }
    }

    data class MusicPlayerState(
        val currentPlayingSongIndex: Int? = null,
        val playerState: MusicPlayerStates? = null,
    )

    fun onGetSession(controllerInfo: MediaSession.ControllerInfo): MediaSession? {
        TODO("Not yet implemented")
    }
}
