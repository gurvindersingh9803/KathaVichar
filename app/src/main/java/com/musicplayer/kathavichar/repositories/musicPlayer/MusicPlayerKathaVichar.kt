package com.musicplayer.kathavichar.repositories.musicPla

import android.app.Application
import android.app.PendingIntent
import android.content.ComponentName
import android.content.Context
import android.graphics.Bitmap
import android.os.Build
import androidx.annotation.OptIn
import androidx.annotation.RequiresApi
import androidx.lifecycle.AndroidViewModel
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.common.Tracks
import androidx.media3.common.util.UnstableApi
import androidx.media3.session.MediaController
import androidx.media3.session.SessionToken
import androidx.media3.ui.PlayerNotificationManager
import com.musicplayer.kathavichar.common.SharedPrefsManager
import com.musicplayer.kathavichar.repositories.musicPlayer.MediaService
import com.musicplayer.kathavichar.repositories.musicPlayer.MusicPlayerStates
import com.google.common.util.concurrent.ListenableFuture
import com.google.common.util.concurrent.MoreExecutors
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import org.koin.java.KoinJavaComponent
import kotlin.coroutines.cancellation.CancellationException

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(UnstableApi::class)
class MusicPlayerKathaVichar(
    private val context: Context,
) : Player.Listener {
    private var isServiceRunning = false
    private var isListenerAdded = false
    var currentMediaItemId = ""
    private lateinit var playerNotification: PlayerNotificationManager
    val _playerStates = MutableStateFlow(MusicPlayerStates.STATE_IDLE)
    private val sharedPreferences: SharedPrefsManager by KoinJavaComponent.inject(SharedPrefsManager::class.java)

    val currentPlaybackPosition: Long
        get() = if (mediaController?.currentPosition!! > 0) mediaController!!.currentPosition else 0L

    val currentTrackDuration: Long
        get() = if (mediaController?.duration!! > 0) mediaController!!.duration else 0L
    var mediaController: MediaController? = null

    private fun stopPlayTimeTracking() {
        // trackingJob?.cancel()
    }

    fun releaseCleanUp() {
        stopPlayTimeTracking()
        // trackingJob?.cancel()
        mediaController?.release()
        isListenerAdded = false
    }
    // fun release() {
    //    stopPlayTimeTracking()
    // Rest of your existing release code...
    // }

    @OptIn(UnstableApi::class)
    fun initMusicPlayer(songsList: MutableList<MediaItem>) {
        handlePlaylistLoading(songsList)
    }

    private fun handlePlaylistLoading(songsList: MutableList<MediaItem>) {
        if (songsList.isEmpty()) {
            println("🎶 Songs list is empty!")
            return
        }

        val isAlreadyPlaying = mediaController?.playbackState == Player.STATE_READY ||
                mediaController?.isPlaying == true

        if (isAlreadyPlaying) {
            updateExistingPlaylist(songsList)
        } else {
            startFreshPlayback(songsList)
        }
    }

    private fun updateExistingPlaylist(songsList: MutableList<MediaItem>) {
        mediaController?.apply {
            val oldUris = currentMediaItem?.mediaId
            val newUris = songsList.firstOrNull()?.mediaId

            if (oldUris != newUris) {
                clearMediaItems()
                setMediaItems(songsList)
                prepare()
                play()
            }
        }
    }

    private fun startFreshPlayback(songsList: MutableList<MediaItem>) {
        mediaController?.apply {
            clearMediaItems()
            addListener(this@MusicPlayerKathaVichar)
            setMediaItems(songsList.toList())
            prepare()
        }
    }

    fun release() {
        // Call this when you're done with the class, e.g., onDestroy or onViewModelCleared
        // job.cancel()
    }

    fun initializeMediaController() {
        if (mediaController == null) {
            println("dgfhyulh")
            val mediaSessionToken = SessionToken(context, ComponentName(context, MediaService::class.java))
            val future = MediaController.Builder(context, mediaSessionToken).buildAsync()
            future.addListener(
                {
                    try {
                        if (future.isCancelled) {
                            println("🚫 MediaController initialization was cancelled.")
                            return@addListener
                        }

                        mediaController = future.get()
                        println("🎶 MediaController Initialized")
                    } catch (e: CancellationException) {
                        println("⚠️ MediaController init cancelled: ${e.message}")
                    } catch (e: Exception) {
                        println("❌ Error initializing MediaController: ${e.message}")
                    }
                },
                MoreExecutors.directExecutor(),
            )
        }
    }

    fun playPause() {
        if (mediaController?.playbackState == Player.STATE_IDLE) {
            mediaController?.prepare()
        }
        mediaController?.playWhenReady = !(mediaController?.playWhenReady ?: false)
    }

    fun releasePlayer() {
        mediaController?.release()
        isListenerAdded = false
    }

    fun seekToPosition(position: Long) {
        mediaController?.seekTo(position)
    }

    fun setUpTrack(
        index: Int,
        isTrackPlay: Boolean,
        isSongRestored: Boolean? = false,
        lastPosition: Long? = 0L,
    ) { if (isTrackPlay) mediaController?.playWhenReady = true
        if (mediaController?.playbackState == Player.STATE_IDLE) mediaController?.prepare()
        // if (isSongRestored == true) {
        // println("restored sadfdsfgsdf $index $isTrackPlay")
        mediaController?.seekTo(index, 0)
        // isSongRestored = false
        // release()
        // } else {
        //   println("not restored sadfdsfgsdf $index $isTrackPlay")
        //  mediaController?.seekTo(index, 0)
        // }
        if (isTrackPlay) mediaController?.playWhenReady = true
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
                stopPlayTimeTracking()
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

                if (mediaController?.playWhenReady == true) {
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
                stopPlayTimeTracking()
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
        println("onPlayWhenReadyChanged ghhjghjddddd $mediaController")
        if (mediaController?.playbackState == Player.STATE_READY) {
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

class MediaControllerViewModel(application: Application) : AndroidViewModel(application) {
    private val context = application.applicationContext
    private var factory: ListenableFuture<MediaController>? = null

    private val _mediaController = MutableStateFlow<MediaController?>(null)
    val mediaController: StateFlow<MediaController?> = _mediaController // Observe in UI

    init {
        initialize()
    }

    @OptIn(androidx.media3.common.util.UnstableApi::class)
    private fun initialize() {
        if (factory == null || factory?.isDone == true) {
            factory = MediaController.Builder(
                context,
                SessionToken(context, ComponentName(context, MediaService::class.java)),
            ).buildAsync()
        }

        factory?.addListener(
            {
                _mediaController.value = factory?.takeIf { it.isDone }?.get()
            },
            MoreExecutors.directExecutor(),
        )
    }

    override fun onCleared() {
        super.onCleared()
        factory?.let { MediaController.releaseFuture(it) }
        _mediaController.value = null
    }
}
