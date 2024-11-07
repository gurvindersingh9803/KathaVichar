package com.example.kathavichar.view.composables.musicPlayer

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.ContentAlpha
import androidx.compose.material.Icon
import androidx.compose.material.LocalContentAlpha
import androidx.compose.material.MaterialTheme
import androidx.compose.material.ProgressIndicatorDefaults
import androidx.compose.material.Slider
import androidx.compose.material.SliderDefaults
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material.icons.rounded.NavigateNext
import androidx.compose.material.icons.rounded.PauseCircleFilled
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.media3.common.MediaItem
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import com.example.kathavichar.R
import kotlinx.coroutines.flow.MutableStateFlow
import org.koin.android.ext.android.inject
import org.koin.java.KoinJavaComponent.inject

@Composable
fun MusicPlayer() {
    Box(modifier = Modifier.fillMaxSize()) {
        Surface(modifier = Modifier.fillMaxSize()) {
            val gradient = Brush.verticalGradient(listOf(MaterialTheme.colors.primary, MaterialTheme.colors.primaryVariant))

            val sliderColors = SliderDefaults.colors(
                thumbColor = MaterialTheme.colors.background,
                activeTrackColor = MaterialTheme.colors.background,
                inactiveTrackColor = MaterialTheme.colors.background.copy(
                    alpha = ProgressIndicatorDefaults.IndicatorBackgroundOpacity,
                ),
            )

            Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center, modifier = Modifier.background(gradient)) {
                Box(
                    modifier = Modifier
                        .clip(MaterialTheme.shapes.medium)
                        .aspectRatio(1f),
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Image(painter = painterResource(id = R.drawable.imag), contentDescription = null, modifier = Modifier.fillMaxWidth())

                        Text(text = "Dasam Granth", style = MaterialTheme.typography.subtitle1, color = MaterialTheme.colors.surface)
                        Text(text = "Subtitle", style = MaterialTheme.typography.body2, color = MaterialTheme.colors.surface)

                        Slider(value = 0f, onValueChange = {}, colors = sliderColors)

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            CompositionLocalProvider(LocalContentAlpha provides ContentAlpha.medium) {
                                Text(
                                    "3:30",
                                    style = MaterialTheme.typography.body2,
                                )
                            }
                            CompositionLocalProvider(LocalContentAlpha provides ContentAlpha.medium) {
                                Text(
                                    "00.10",
                                    style = MaterialTheme.typography.body2,
                                )
                            }
                        }

                        Row(
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.ArrowBack,
                                contentDescription = "Skip Previous",
                                modifier = Modifier
                                    .clip(CircleShape)
                                    .clickable(onClick = {})
                                    .padding(12.dp)
                                    .size(48.dp),
                            )

                            Icon(
                                imageVector = Icons.Rounded.PauseCircleFilled,
                                contentDescription = "Skip Previous",
                                modifier = Modifier
                                    .clip(CircleShape)
                                    .clickable(onClick = {})
                                    .padding(12.dp)
                                    .size(62.dp),
                            )

                            Icon(
                                imageVector = Icons.Rounded.NavigateNext,
                                contentDescription = "Skip Previous",
                                modifier = Modifier
                                    .clip(CircleShape)
                                    .clickable(onClick = {})
                                    .padding(12.dp)
                                    .size(48.dp),
                            )
                        }
                    }
                }
            }
        }
    }
}


/**
 * A custom player class that provides several convenience methods for
 * controlling playback and monitoring the state of an underlying ExoPlayer.
 *
 * @param player The ExoPlayer instance that this class wraps.
 */
class MyPlayer : Player.Listener {
    // Inject ExoPlayer instance
    private val player: ExoPlayer by inject(ExoPlayer::class.java)

    /**
     * A state flow that emits the current playback state of the player.
     */
    val playerState = MutableStateFlow(MusicPlayerStates.STATE_IDLE)

    /**
     * The current playback position in milliseconds. If the player's position
     * is negative, this returns 0.
     */
    val currentPlaybackPosition: Long
        get() = if (player.currentPosition > 0) player.currentPosition else 0L

    /**
     * The duration of the current track in milliseconds. If the track's duration
     * is negative, this returns 0.
     */
    val currentTrackDuration: Long
        get() = if (player.duration > 0) player.duration else 0L

    /**
     * Initializes the player with a list of media items.
     *
     * @param trackList The list of media items to play.
     */
    fun iniPlayer(trackList: MutableList<MediaItem>) {
        player.addListener(this)
        player.setMediaItems(trackList)
        player.prepare()
    }

    /**
     * Sets up the player to start playback of the track at the specified index.
     *
     * @param index The index of the track in the playlist.
     * @param isTrackPlay If true, playback will start immediately.
     */
    fun setUpTrack(index: Int, isTrackPlay: Boolean) {
        if (player.playbackState == Player.STATE_IDLE) player.prepare()
        player.seekTo(index, 0)
        if (isTrackPlay) player.playWhenReady = true
    }

    /**
     * Toggles the playback state between playing and paused.
     */
    fun playPause() {
        if (player.playbackState == Player.STATE_IDLE) player.prepare()
        player.playWhenReady = !player.playWhenReady
    }

    /**
     * Releases the player, freeing any resources it holds.
     */
    fun releasePlayer() {
        player.release()
    }

    /**
     * Seeks to the specified position in the current track.
     *
     * @param position The position to seek to, in milliseconds.
     */
    fun seekToPosition(position: Long) {
        player.seekTo(position)
    }

    // Overrides for Player.Listener follow...

    /**
     * Called when a player error occurs. This implementation emits the
     * STATE_ERROR state to the playerState flow.
     */
    override fun onPlayerError(error: PlaybackException) {
        super.onPlayerError(error)
        playerState.tryEmit(MusicPlayerStates.STATE_ERROR)
    }

    /**
     * Called when the player's playWhenReady state changes. This implementation
     * emits the STATE_PLAYING or STATE_PAUSE state to the playerState flow
     * depending on the new playWhenReady state and the current playback state.
     */
    override fun onPlayWhenReadyChanged(playWhenReady: Boolean, reason: Int) {
        if (player.playbackState == Player.STATE_READY) {
            if (playWhenReady) {
                playerState.tryEmit(MusicPlayerStates.STATE_PLAYING)
            } else {
                playerState.tryEmit(MusicPlayerStates.STATE_PAUSE)
            }
        }
    }

    /**
     * Called when the player transitions to a new media item. This implementation
     * emits the STATE_NEXT_TRACK and STATE_PLAYING states to the playerState flow
     * if the transition was automatic.
     */
    override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) {
        super.onMediaItemTransition(mediaItem, reason)
        if (reason == Player.MEDIA_ITEM_TRANSITION_REASON_AUTO) {
            playerState.tryEmit(MusicPlayerStates.STATE_NEXT_TRACK)
            playerState.tryEmit(MusicPlayerStates.STATE_PLAYING)
        }
    }

    /**
     * Called when the player's playback state changes. This implementation emits
     * a state to the playerState flow corresponding to the new playback state.
     */
    override fun onPlaybackStateChanged(playbackState: Int) {
        when (playbackState) {
            Player.STATE_IDLE -> {
                playerState.tryEmit(MusicPlayerStates.STATE_IDLE)
            }

            Player.STATE_BUFFERING -> {
                playerState.tryEmit(MusicPlayerStates.STATE_BUFFERING)
            }

            Player.STATE_READY -> {
                playerState.tryEmit(MusicPlayerStates.STATE_READY)
                if (player.playWhenReady) {
                    playerState.tryEmit(MusicPlayerStates.STATE_PLAYING)
                } else {
                    playerState.tryEmit(MusicPlayerStates.STATE_PAUSE)
                }
            }

            Player.STATE_ENDED -> {
                playerState.tryEmit(MusicPlayerStates.STATE_END)
            }
        }
    }
}
