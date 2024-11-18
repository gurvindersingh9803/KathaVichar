package com.example.kathavichar.viewModel

import android.media.session.PlaybackState
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import com.example.kathavichar.model.Song
import com.example.kathavichar.network.ServerResponse
import com.example.kathavichar.repositories.SongsListFirebase
import com.example.kathavichar.repositories.musicPlayer.MusicPlayerEvents
import com.example.kathavichar.repositories.musicPlayer.MusicPlayerKathaVichar
import com.example.kathavichar.repositories.musicPlayer.MusicPlayerStates
import com.example.kathavichar.repositories.musicPlayer.PlayerBackState
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.koin.java.KoinJavaComponent

class SongsViewModel :
    ViewModel(),
    MusicPlayerEvents {
    private val _uiStateSongs: MutableStateFlow<ServerResponse<MutableList<Song>>> = MutableStateFlow(ServerResponse.isLoading())
    val uiStateSongs = _uiStateSongs.asStateFlow()
    private val sonsListFirebase: SongsListFirebase by KoinJavaComponent.inject(SongsListFirebase::class.java)

    private val _songs = mutableStateListOf<Song>()

    /**
     * An immutable snapshot of the current list of tracks.
     */
    val songs: List<Song> get() = _songs

    val subscription: CompositeDisposable = CompositeDisposable()

    private val musicPlayerKathaVichar: MusicPlayerKathaVichar by KoinJavaComponent.inject(
        MusicPlayerKathaVichar::class.java,
    )

    private var selectedTrackIndex: Int by mutableStateOf(-1)
    var selectedTrack: Song? by mutableStateOf(null)
        private set

    private var isTrackPlay: Boolean = false

    private var isAuto: Boolean = false

    private val playbackStateFlowJob = viewModelScope

    /**
     * A private [MutableStateFlow] that holds the current [PlaybackState].
     * It is used to emit updates about the playback state to observers.
     */

    private val _playbackState = MutableLiveData(PlayerBackState(0L, 0L))
    val playerStates: LiveData<PlayerBackState> get() = _playbackState
    /*private val _playbackState = MutableStateFlow(PlayerBackState(0L, 0L))

     */

    /**
     * A public property that exposes the [_playbackState] as an immutable [StateFlow] for observers.
     */
    /*
    val playbackState: StateFlow<PlayerBackState> get() = _playbackState*/

    init {
        viewModelScope.launch {
            getSongs("Maskeen Ji")
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun getSongs(artistName: String) {
        viewModelScope.launch {
            subscription.add(
                sonsListFirebase
                    .getSongsList(artistName)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe({
                        val songList =
                            it.map { rawSong ->
                                Song
                                    .Builder()
                                    .title(rawSong.title)
                                    .audioUrl(rawSong.audioUrl)
                                    .imgUrl(rawSong.imgUrl)
                                    .artistName(rawSong.artistName)
                                    .build()
                            }
                        println("argts $songList")

                        // Add to _songs and initialize the music player
                        _songs.addAll(songList)
                        musicPlayerKathaVichar.initMusicPlayer(songs.toMediaItemListWithMetadata())
                        observeMusicPlayerState()
                    }, {
                        Log.i("edfgwegf", it.toString())
                    }),
            )
        }
    }

    override fun onPlayPauseClicked() {
        musicPlayerKathaVichar.playPause()
    }

    override fun onPreviousClicked() {
        if (selectedTrackIndex > 0) onTrackSelected(selectedTrackIndex - 1)
    }

    override fun onNextClicked() {
        if (selectedTrackIndex < songs.size - 1) onTrackSelected(selectedTrackIndex + 1)
    }

    override fun onTrackClicked(song: Song) {
        println("onTrackClicked")
        onTrackSelected(songs.indexOf(song))
    }

    override fun onSeekBarPositionChanged(position: Long) {
        viewModelScope.launch { musicPlayerKathaVichar.seekToPosition(position) }
    }

    private fun observeMusicPlayerState() {
        viewModelScope.launch {
            musicPlayerKathaVichar.playerStates.observeForever { state ->
                if (state == MusicPlayerStates.STATE_NEXT_TRACK) {
                    isAuto = true
                    onNextClicked()
                } else {
                    updateState(state)
                }
            }
        }
    }

    private fun onTrackSelected(index: Int) {
        if (selectedTrackIndex == -1) isTrackPlay = true
        if (selectedTrackIndex == -1 || selectedTrackIndex != index) {
            selectedTrackIndex = index
            _songs.resetTracks()
            setUpTrack()
        }
    }

    private fun MutableList<Song>.resetTracks() {
        this.forEach { track ->
            track.isSelected = false
            track.state = MusicPlayerStates.STATE_IDLE
        }
    }

    private fun setUpTrack() {
        if (!isAuto) musicPlayerKathaVichar.setUpTrack(selectedTrackIndex, isTrackPlay)
        isAuto = false
    }

    private fun updateState(state: MusicPlayerStates) {
        if (selectedTrackIndex != -1) {
            isTrackPlay = state == MusicPlayerStates.STATE_PLAYING || state == MusicPlayerStates.STATE_BUFFERING
            _songs[selectedTrackIndex].state = state
            _songs[selectedTrackIndex].isSelected = true
            selectedTrack = null
            selectedTrack = songs[selectedTrackIndex]
            updatePlaybackState(state)

            if (state == MusicPlayerStates.STATE_END) onTrackSelected(0)
        }
    }

    private fun updatePlaybackState(state: MusicPlayerStates) {
        // TODO: store this scope in a var and destroy it when used may be.
        viewModelScope.launch {
            do {
                _playbackState.postValue(
                    PlayerBackState(
                        currentPlayBackPosition = musicPlayerKathaVichar.currentPlaybackPosition,
                        currentTrackDuration = musicPlayerKathaVichar.currentTrackDuration,
                    ),
                )
                delay(1000)
            } while (state == MusicPlayerStates.STATE_PLAYING)
        }
    }

    /**
     * Converts a list of [Track] objects into a mutable list of [MediaItem] objects.
     *
     * @return A mutable list of [MediaItem] objects.
     */
    fun List<Song>.toMediaItemList(): MutableList<MediaItem> =
        this
            .map {
                MediaItem.fromUri(it.audioUrl)
            }.toMutableList()

    fun List<Song>.toMediaItemListWithMetadata(): MutableList<MediaItem> =
        this
            .map { song ->
                MediaItem
                    .Builder()
                    .setUri(song.audioUrl)
                    .setMediaMetadata(
                        MediaMetadata
                            .Builder()
                            .setTitle(song.title)
                            .build(),
                    ).build()
            }.toMutableList()

    override fun onCleared() {
        super.onCleared()
        musicPlayerKathaVichar.releasePlayer()
    }
}
