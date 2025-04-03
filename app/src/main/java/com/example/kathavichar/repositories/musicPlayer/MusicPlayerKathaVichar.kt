package com.example.kathavichar.repositories.musicPlayer

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.util.Log
import androidx.annotation.OptIn
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.common.Tracks
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.session.MediaSession
import androidx.media3.session.MediaSessionService
import androidx.media3.ui.PlayerNotificationManager
import com.example.kathavichar.R
import com.example.kathavichar.common.Constants
import kotlinx.coroutines.flow.MutableStateFlow

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(UnstableApi::class)
class MusicPlayerKathaVichar(
    private val mediaService: MediaService,
    private val context: Context,
) : Player.Listener {
    private var isServiceRunning = false
    private var isListenerAdded = false
    var currentMediaItemId = ""
    private lateinit var playerNotification: PlayerNotificationManager
    val _playerStates = MutableStateFlow(MusicPlayerStates.STATE_IDLE)
    val currentPlaybackPosition: Long
        get() = if (mediaService.exoPlayer.currentPosition > 0) mediaService.exoPlayer.currentPosition else 0L

    val currentTrackDuration: Long
        get() = if (mediaService.exoPlayer.duration > 0) mediaService.exoPlayer.duration else 0L

    @OptIn(UnstableApi::class)
    fun initMusicPlayer(songsList: MutableList<MediaItem>) {
        mediaService.exoPlayer.clearMediaItems()
        if (!isListenerAdded) {
            mediaService.exoPlayer.addListener(this)
            isListenerAdded = true
        }
        mediaService.exoPlayer.setMediaItems(songsList)
        mediaService.exoPlayer.prepare()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    @UnstableApi
    fun startMusicNotificationService(
        mediaSessionService: MediaSessionService,
        mediaSession: MediaSession,
    ) {
        createNotificationChannel()
        startForegroundMusicService(mediaSessionService)
        buildMusicNotification(mediaSession)
    }

    private fun createStubNotification(): Notification {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createNotificationChannel() // Ensure channel exists
        }

        return NotificationCompat.Builder(context, Constants.NOTIFICATION_CHANNEL_ID)
            .setContentTitle("Music Player")
            .setContentText("Playing music")
            .setSmallIcon(R.drawable.headset) // Ensure this icon exists in res/drawable
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .build()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createNotificationChannel() {
        val channel = NotificationChannel(
            Constants.NOTIFICATION_CHANNEL_ID, // Use the same ID in notification
            "Music Playback",
            NotificationManager.IMPORTANCE_LOW, // Low importance to avoid intrusive behavior
        )
        channel.description = "Channel for music playback notifications"

        val notificationManager = context.getSystemService(NotificationManager::class.java)
        notificationManager.createNotificationChannel(channel)
    }

    // Getter for ExoPlayer instance
    fun getExoPlayer(): ExoPlayer {
        return mediaService.exoPlayer
    }

    fun getCurrentMediaItem(): MediaItem? {
        mediaService.exoPlayer.currentMediaItem?.mediaMetadata?.let { mediaMetadata ->
            println("MediaMetadata Properties:")
            println("Title: ${mediaMetadata.title}")
            println("Artist: ${mediaMetadata.artist}")
            println("Album Title: ${mediaMetadata.albumTitle}")
            println("Display Title: ${mediaMetadata.displayTitle}")
            println("Subtitle: ${mediaMetadata.subtitle}")
            println("Description: ${mediaMetadata.description}")
            println("Artwork URI: ${mediaMetadata.artworkUri}")
            println("Extras: ${mediaMetadata.extras}") // Print extras if any
            println("Track Number: ${mediaMetadata.trackNumber}")
            println("Total Track Count: ${mediaMetadata.totalTrackCount}")
            println("Folder Type: ${mediaMetadata.folderType}")
            println("Is Playable: ${mediaMetadata.isPlayable}")
            println("Is Browsable: ${mediaMetadata.isBrowsable}")
            println("Release Year: ${mediaMetadata.releaseYear}")
            println("Overall Rating: ${mediaMetadata.overallRating}")
            println("User Rating: ${mediaMetadata.userRating}")
            println("Media Type: ${mediaMetadata.mediaType}")
        }
        return mediaService.exoPlayer.currentMediaItem
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
        playerNotification = PlayerNotificationManager
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
                it.setPlayer(mediaService.exoPlayer)
            }
    }

    fun playPause() {
        if (mediaService.exoPlayer.playbackState == Player.STATE_IDLE) mediaService.exoPlayer.prepare()
        mediaService.exoPlayer.playWhenReady = !mediaService.exoPlayer.playWhenReady
    }

    fun releasePlayer() {
        mediaService.exoPlayer.release()
        isListenerAdded = false
    }

    fun seekToPosition(position: Long) {
        mediaService.exoPlayer.seekTo(position)
    }

    fun setUpTrack(
        index: Int,
        isTrackPlay: Boolean,
    ) { if (isTrackPlay) mediaService.exoPlayer.playWhenReady = true
        println("sadfdsfgsdf $index $isTrackPlay")
        if (mediaService.exoPlayer.playbackState == Player.STATE_IDLE) mediaService.exoPlayer.prepare()
        mediaService.exoPlayer.seekTo(index, 0)
        if (isTrackPlay) mediaService.exoPlayer.playWhenReady = true
    }

    override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) {
        super.onMediaItemTransition(mediaItem, reason)

        currentMediaItemId = mediaItem?.mediaId.toString()

        println("yutuyrytry dsqwafa $currentMediaItemId")
        when (reason) {
            Player.MEDIA_ITEM_TRANSITION_REASON_AUTO -> {
                // Automatic transition to the next track
                println("MEDIA_ITEM_TRANSITION_REASON_AUTO onMediaItemTransition")
                _playerStates.tryEmit(MusicPlayerStates.STATE_NEXT_TRACK)
            }
            Player.MEDIA_ITEM_TRANSITION_REASON_SEEK -> {
                println("MEDIA_ITEM_TRANSITION_REASON_SEEK onMediaItemTransition")
                // Transition caused by seeking (e.g., next/previous button)
                _playerStates.tryEmit(MusicPlayerStates.STATE_TRACK_CHANGED)
            }
            Player.MEDIA_ITEM_TRANSITION_REASON_PLAYLIST_CHANGED -> {
                println("MEDIA_ITEM_TRANSITION_REASON_PLAYLIST_CHANGED onMediaItemTransition")
                // Transition caused by a playlist change
                _playerStates.tryEmit(MusicPlayerStates.STATE_TRACK_CHANGED)
            }

            Player.MEDIA_ITEM_TRANSITION_REASON_REPEAT -> {
                TODO()
            }
        }
    }

    override fun onTracksChanged(tracks: Tracks) {
        super.onTracksChanged(tracks)
    }

    override fun onPlaybackStateChanged(playbackState: Int) {
        println("onPlaybackStateChanged ghhjghj $playbackState")
        when (playbackState) {
            Player.STATE_IDLE -> {
                _playerStates.tryEmit(
                    MusicPlayerStates.STATE_IDLE,

                )

                // _playerState.postValue(MusicPlayerStates.STATE_IDLE)
            }

            Player.STATE_BUFFERING -> {
                _playerStates.tryEmit(

                    MusicPlayerStates.STATE_BUFFERING,

                )

                // _playerState.postValue(MusicPlayerStates.STATE_BUFFERING)
            }

            Player.STATE_READY -> {
                _playerStates.tryEmit(
                    MusicPlayerStates.STATE_READY,

                )

                // _playerState.postValue(MusicPlayerStates.STATE_READY)

                if (mediaService.exoPlayer.playWhenReady) {
                    _playerStates.tryEmit(
                        MusicPlayerStates.STATE_PLAYING,

                    )

                    // _playerState.postValue(MusicPlayerStates.STATE_PLAYING)
                } else {
                    _playerStates.tryEmit(
                        MusicPlayerStates.STATE_PAUSE,

                    )

                    //  _playerState.postValue(MusicPlayerStates.STATE_PAUSE)
                }
            }

            Player.STATE_ENDED -> {
                _playerStates.tryEmit(
                    MusicPlayerStates.STATE_END,

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
        if (mediaService.exoPlayer.playbackState == Player.STATE_READY) {
            if (playWhenReady) {
                _playerStates.tryEmit(
                    MusicPlayerStates.STATE_PLAYING,

                )

                //  _playerState.postValue(MusicPlayerStates.STATE_PLAYING)
            } else {
                _playerStates.tryEmit(
                    MusicPlayerStates.STATE_PAUSE,

                )

                //  _playerState.postValue(MusicPlayerStates.STATE_PAUSE)
            }
        }
    }

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
}
