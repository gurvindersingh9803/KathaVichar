package com.example.kathavichar.repositories.musicPla

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
import com.example.kathavichar.common.SharedPrefsManager
import com.example.kathavichar.repositories.musicPlayer.MediaService
import com.example.kathavichar.repositories.musicPlayer.MusicPlayerStates
import com.google.common.util.concurrent.ListenableFuture
import com.google.common.util.concurrent.MoreExecutors
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
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

    private val job = SupervisorJob()
    private val scope = CoroutineScope(Dispatchers.Main + job)

    init {
        startPlaybackObserver()
    }

    private fun startPlaybackObserver() {
        scope.launch {
            //observePlaybackState()
        }
    }

    @OptIn(UnstableApi::class)
    fun initMusicPlayer(songsList: MutableList<MediaItem>) {
        try {
            println("🎶 Songs list $songsList")

            // Exit early if the list is empty
            if (songsList.isEmpty()) {
                println("🎶 Songs list is empty!")
                return
            }

            // Initialize the MediaController asynchronously
            initializeMediaController {
                if (mediaController == null) {
                    println("⚠️ MediaController is null!")
                    return@initializeMediaController
                }

                // Check if player is already initialized and playing something
                val isAlreadyPlaying = mediaController?.playbackState == Player.STATE_READY ||
                    mediaController?.isPlaying == true

                // If it's already playing, just update the playlist (if it's different)
                if (isAlreadyPlaying) {
                    println("🎧 Player is already playing. Updating song list...")

                    mediaController?.apply {
                        val oldUris = mediaController?.currentMediaItem?.mediaId
                        val newUris = songsList.get(0).mediaId

                        println("fghdfghf $oldUris $newUris")
                        if (oldUris != newUris) {
                            clearMediaItems()
                            setMediaItems(songsList)
                            prepare()
                            play() // optional: restart playback if list is updated
                            println("✅ Playlist updated while playing. mnmn")
                        } else {
                            println("🔁 Same playlist. No changes made. mnmn")
                        }
                    }
                } else {
                    // Not playing anything: fresh start
                    mediaController?.apply {
                        clearMediaItems()
                        addListener(this@MusicPlayerKathaVichar)
                        println("🎵 Setting media items to controller...")
                        setMediaItems(songsList.toList())
                        prepare()
                    }

                    println("🎵 Initialized music player with ${mediaController?.mediaItemCount} items.")
                }
            }
        } catch (e: Exception) {
            println("Error initializing music player: ${e.message}")
        }
    }

    // Initialize the MediaController

       /* if (songsList.isEmpty()) {
            println("songsList is EMPTY! No media items to play.")
            return
        }

        println("🎵 Initializing music player with ${songsList.size} items")

        songsList.forEachIndexed { index, mediaItem ->
            println("🎶 Track $index: ${mediaItem.mediaId} - ${mediaItem.localConfiguration?.uri}")
        }

        mediaController?.clearMediaItems()
        mediaController?.setMediaItems(songsList)
        mediaController?.prepare()
        mediaController?.playWhenReady = true
        println("✅ Media items added. Total count: ${mediaController?.mediaItemCount}")*/

        /*mediaController?.clearMediaItems()
        if (!isListenerAdded) {
            mediaController?.addListener(this)
            isListenerAdded = true
        }

        mediaController?.setMediaItems(songsList)
        mediaController?.prepare()*/

    suspend fun observePlaybackState() {
        withContext(Dispatchers.Main) {
            while (isActive) {
                mediaController?.let {
                    val currentMediaId = it.currentMediaItem?.mediaId
                    val position = it.currentPosition
                    if (currentMediaId != null) {
                        println("ghfgnjj $currentMediaId $position")
                        savePlaybackState(songId = currentMediaId, artistName = it.currentMediaItem!!.mediaMetadata.artist.toString(), position = position)
                    }
                }
                delay(1000)
            }
        }
    }

    fun release() {
        // Call this when you're done with the class, e.g., onDestroy or onViewModelCleared
        job.cancel()
    }

    fun savePlaybackState(songId: String, artistName: String, position: Long) {
        try {
            println("dfghjdfgthd $songId $artistName $position")
            sharedPreferences.saveString("LAST_PLAYING_SONG_ID", songId)
            sharedPreferences.saveString("LAST_PLAYING_PLAYLIST", artistName)
            sharedPreferences.saveLong("LAST_PLAYING_POSITION", position)
        } catch (e: Exception) {
            println("SharedPreferences saving error: $e")
        }
    }
    private fun initializeMediaController(onInitialized: () -> Unit) {
        if (mediaController == null) {
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

                        onInitialized()
                    } catch (e: CancellationException) {
                        println("⚠️ MediaController init cancelled: ${e.message}")
                    } catch (e: Exception) {
                        println("❌ Error initializing MediaController: ${e.message}")
                    }
                },
                MoreExecutors.directExecutor(),
            )
        } else {
            // Already initialized, proceed with callback
            onInitialized()
        }
    }

    /*private fun createStubNotification(): Notification {
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
                it.setPlayer(mediaController)
            }
    }*/

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
        isSongRestored: Boolean?,
        lastPosition: Long,
    ) { if (isTrackPlay) mediaController?.playWhenReady = true
        if (mediaController?.playbackState == Player.STATE_IDLE) mediaController?.prepare()
        if (isSongRestored == true) {
            println("restored sadfdsfgsdf $index $isTrackPlay")
            mediaController?.seekTo(index, lastPosition)
            //isSongRestored = false
            release()
        } else {
            println("not restored sadfdsfgsdf $index $isTrackPlay")
            mediaController?.seekTo(index, 0)
        }
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
