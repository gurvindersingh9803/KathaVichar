package com.example.kathavichar.viewModel

import android.media.session.PlaybackState
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import org.koin.java.KoinJavaComponent

class SongsViewModel(
    savedStateHandle: SavedStateHandle,
) : ViewModel(),
    MusicPlayerEvents {
    private val _uiStateSongs: MutableStateFlow<ServerResponse<MutableList<Song>>> = MutableStateFlow(ServerResponse.isLoading())
    val uiStateSongs = _uiStateSongs.asStateFlow()
    private val sonsListFirebase: SongsListFirebase by KoinJavaComponent.inject(SongsListFirebase::class.java)

    private val _songs = mutableStateListOf<Song>()

    /**
     * An immutable snapshot of the current list of tracks.
     */
    val songs: List<Song> get() = _songs

    private val _currentplayingsongs = mutableStateListOf<Song>()

    /**
     * An immutable snapshot of the current list of tracks.
     */
    val currentplayingsongs: List<Song> get() = _currentplayingsongs

    val subscription: CompositeDisposable = CompositeDisposable()

    private val musicPlayerKathaVichar: MusicPlayerKathaVichar by KoinJavaComponent.inject(
        MusicPlayerKathaVichar::class.java,
    )

    private var selectedTrackIndex: Int by mutableStateOf(-1)
    var selectedTrack: Song? by mutableStateOf(null)
        private set

    var whichArtistSelected: String? by mutableStateOf(null)
        private set

    private var isTrackPlay: Boolean = false

    private var isAuto: Boolean = false

    private var playbackStateJob: Job? = null

    /**
     * A private [MutableStateFlow] that holds the current [PlaybackState].
     * It is used to emit updates about the playback state to observers.
     */

    private val _playbackState = MutableStateFlow(PlayerBackState(0L, 0L))
    /**
     * A public property that exposes the [_playbackState] as an immutable [StateFlow] for observers.
     */
    val playbackState: StateFlow<PlayerBackState> get() = _playbackState

    // val playerStates: LiveData<PlayerBackState> get() = _playbackState
    /*private val _playbackState = MutableStateFlow(PlayerBackState(0L, 0L))

     */

    /**
     * A public property that exposes the [_playbackState] as an immutable [StateFlow] for observers.
     */
    /*
    val playbackState: StateFlow<PlayerBackState> get() = _playbackState*/

    init {
        viewModelScope.launch {
            // observeMusicPlayerState()
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun getSongs(artistName: String) {
        println("egfrdegf ${selectedTrack}")
        viewModelScope.launch {
            whichArtistSelected = artistName
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
                            if (songList.isNotEmpty()) {
                                selectedTrackIndex = -1
                                _songs.clear()
                                _songs.addAll(songList)
                                Log.i("edfgwegf", songs.toMediaItemListWithMetadata().toString())
                                _uiStateSongs.tryEmit(ServerResponse.onSuccess(songList.toMutableList()))
                                observeMusicPlayerState()

                            }
                        }, {
                            Log.i("edfgwegf", it.toString())
                        }),
                )

        }
    }

    override fun onPlayPauseClicked() {
        println("qrfeqw")
        musicPlayerKathaVichar.playPause()
    }

    override fun onPreviousClicked() {
        if (selectedTrackIndex > 0) onTrackSelected(selectedTrackIndex - 1)
    }

    override fun onNextClicked(isBottomClick: Boolean, song: Song?) {
        if(isBottomClick && song != null) {
            println("fsdgsdgf rfrfrfr ${isBottomClick} ${selectedTrackIndex} ${currentplayingsongs.size}")
            if (selectedTrackIndex < currentplayingsongs.size - 1)
                onTrackSelected(currentplayingsongs.indexOf(song) + 1, isBottomClick)
        } else {
            println("fsdgsdgf iupioup ${isBottomClick} ${selectedTrack?.artistName} $whichArtistSelected")
            if (selectedTrackIndex < songs.size - 1)
                onTrackSelected(selectedTrackIndex + 1, isBottomClick)
        }


/*
        if (selectedTrack?.artistName == whichArtistSelected) {
            // change the UI and params
            if (selectedTrackIndex < songs.size - 1)
                onTrackSelected(selectedTrackIndex + 1)
        } else {
            // get index from current Playinglist
            println("fsdgsdgf else ${currentplayingsongs.indexOf(song)}")
*/




      //  }

    }

    override fun onTrackClicked(song: Song) {
        println("onTrackClicked $whichArtistSelected ${selectedTrack?.artistName} ${currentplayingsongs.indexOf(song)}")
        _currentplayingsongs.clear()
        _currentplayingsongs.addAll(songs)
        // selectedTrackIndex = currentplayingsongs.indexOf(song)
        onTrackSelected(currentplayingsongs.indexOf(song))
    }

    override fun onSeekBarPositionChanged(position: Long) {
        viewModelScope.launch { musicPlayerKathaVichar.seekToPosition(position) }
    }

    private fun observeMusicPlayerState() {
        viewModelScope.launch {
            musicPlayerKathaVichar._playerStates.collect { state ->
                println("sdfghdf $state")
                        updateState(state)
            }
        }
    }

    private fun onTrackSelected(index: Int, isBottomClick: Boolean = false) {

        if (isBottomClick || selectedTrack == null) {
            println("jhjh currentplayingsongs $currentplayingsongs")
            if (selectedTrackIndex == -1 || selectedTrackIndex != index) {
                isTrackPlay = true
                selectedTrackIndex = index
                _currentplayingsongs.resetTracks()
                _currentplayingsongs[selectedTrackIndex].isSelected = true
                selectedTrack = currentplayingsongs[selectedTrackIndex]
                setUpTrack()
            }
        } else {
           // _currentplayingsongs.clear()
            println("jhjh songss $currentplayingsongs")
            if (selectedTrackIndex == -1 || selectedTrackIndex != index) {
                isTrackPlay = true
                selectedTrackIndex = index
                _songs.resetTracks()
                _songs[selectedTrackIndex].isSelected = true
                selectedTrack = songs[selectedTrackIndex]
                setUpTrack()
            }
        }

        /*if (selectedTrackIndex == -1 || selectedTrackIndex != index) {
        isTrackPlay = true
        selectedTrackIndex = index
        _currentplayingsongs.resetTracks()
        _currentplayingsongs[selectedTrackIndex].isSelected = true
        selectedTrack = currentplayingsongs[selectedTrackIndex]
        setUpTrack()
        }*/

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

    /*private fun updateState(state: MusicPlayerStates) {
        println("erfergfwe $state $selectedTrackIndex")
        if (selectedTrackIndex != -1) {
            _songs.resetTracks()
            isTrackPlay = state == MusicPlayerStates.STATE_PLAYING
            _songs[selectedTrackIndex].state = state
            _songs[selectedTrackIndex].isSelected = true
            selectedTrack = null
            selectedTrack = songs[selectedTrackIndex]
            updatePlaybackState(state)

            if (state == MusicPlayerStates.STATE_END) onTrackSelected(0)
        }
    }*/

    private fun updateState(state: MusicPlayerStates) {
        println("rgthger $state $selectedTrackIndex")
        if (selectedTrackIndex != -1) {
            isTrackPlay = state == MusicPlayerStates.STATE_PLAYING
            _songs[selectedTrackIndex].state = state
            _songs[selectedTrackIndex].isSelected = true
            selectedTrack = null
            selectedTrack = songs[selectedTrackIndex]

            updatePlaybackState(state)
            if (state == MusicPlayerStates.STATE_NEXT_TRACK) {
               //  isAuto = true
                onNextClicked()
            }

            if (state == MusicPlayerStates.STATE_TRACK_CHANGED) {
                updateSelectedTrackIndex()
            }
            if (state == MusicPlayerStates.STATE_END) onTrackSelected(0)
        }
    }

    private fun updateSelectedTrackIndex() {
        val currentMediaItem = musicPlayerKathaVichar.getCurrentMediaItem()
        if (currentMediaItem != null) {
            val index = songs.toMediaItemListWithMetadata().indexOfFirst { song ->
                println("regfghyy ${currentMediaItem.mediaMetadata.genre} ${song.mediaMetadata.genre}")
                song.mediaMetadata.genre == currentMediaItem.mediaMetadata.genre
            }

            if (index != -1) {
                println("sdgfwsd $index")
                selectedTrackIndex = index
                selectedTrack = songs[selectedTrackIndex]
                _songs.resetTracks()
                _songs[selectedTrackIndex].isSelected = true
            }
        }
    }

    private fun updatePlaybackState(state: MusicPlayerStates) {
        // TODO: store this scope in a var and destroy it when used may be.
        playbackStateJob?.cancel()
        playbackStateJob =
            viewModelScope.launch {
                do {
                    _playbackState.tryEmit(
                        PlayerBackState(
                            currentPlayBackPosition = musicPlayerKathaVichar.currentPlaybackPosition,
                            currentTrackDuration = musicPlayerKathaVichar.currentTrackDuration,
                        ),
                    )
                    delay(1000)
                } while (state == MusicPlayerStates.STATE_PLAYING && isActive)
            }
    }

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
                            .setArtist(song.artistName)
                            .setGenre(song.audioUrl)
                            .build(),
                    ).build()
            }.toMutableList()

    override fun onCleared() {
        super.onCleared()
        println("kjkhlkj")
        musicPlayerKathaVichar.releasePlayer()
    }
}
