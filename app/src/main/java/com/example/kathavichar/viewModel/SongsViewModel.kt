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
import com.example.kathavichar.common.SharedPrefsManager
import com.example.kathavichar.model.Songs
import com.example.kathavichar.network.ServerResponse
import com.example.kathavichar.repositories.SongsDataRepository
import com.example.kathavichar.repositories.musicPla.MusicPlayerKathaVichar
import com.example.kathavichar.repositories.musicPlayer.MusicPlayerEvents
import com.example.kathavichar.repositories.musicPlayer.MusicPlayerStates
import com.example.kathavichar.repositories.musicPlayer.PlayerBackState
import io.reactivex.disposables.CompositeDisposable
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import org.koin.java.KoinJavaComponent.inject

class SongsViewModel(
    private val savedStateHandle: SavedStateHandle,
) : ViewModel(),
    MusicPlayerEvents {
    private val _uiStateSongs: MutableStateFlow<ServerResponse<MutableList<Songs>>> =
        MutableStateFlow(ServerResponse.isLoading())
    val uiStateSongs = _uiStateSongs.asStateFlow()
    private val _songs = mutableStateListOf<Songs>()

    private val songsDataRepository: SongsDataRepository by inject(SongsDataRepository::class.java)

    // Save the current playback state (song ID, playing status, etc.)
    var isPlaybackRestored = false

    private val sharedPreferences: SharedPrefsManager by inject(SharedPrefsManager::class.java)

    // TODO: stop this function to call unnecessary until restore is required.
    fun restorePlaybackState() {

        val currentPosition = musicPlayerKathaVichar.mediaController?.currentPosition
        val songId = musicPlayerKathaVichar.mediaController?.currentMediaItem?.mediaId
        val artistId = musicPlayerKathaVichar.mediaController?.currentMediaItem?.mediaMetadata?.artist
        println("sdfgdsfgsfdg $currentPosition $songId $artistId")

        viewModelScope.launch {
            val lastSongId = sharedPreferences.getString("LAST_PLAYING_SONG_ID", null)
            val lastPlaylistName = sharedPreferences.getString("LAST_PLAYING_PLAYLIST", null)
            val lastPosition = sharedPreferences.getLong("LAST_PLAYING_POSITION", 0L)
            println("sdfgdsfgsfdg 1 $lastSongId $lastPlaylistName $lastPosition")

            // Restore only if we have valid saved data
            if (currentPosition != null && songId != null && artistId != null) {
                // Check network before fetching playlist
                println("sdfgdsfgsfdg 1 $lastSongId $lastPlaylistName $lastPosition")
                val savedPlaylist = songsDataRepository.fetchSongs(artistName = artistId.toString())

                // Ensure the playlist is not empty before proceeding
                if (savedPlaylist.isNotEmpty()) {
                    println("sdfgdsfgsfdg 2 ${lastSongId}")
                    val restoredIndex = savedPlaylist.indexOfFirst { it.id == songId }

                    if (restoredIndex != -1) {
                        _currentplayingsongs.clear()
                        _currentplayingsongs.addAll(savedPlaylist)
                        onTrackSelected(restoredIndex, true, currentPosition)
                        observeMusicPlayerState()

                        // Restore playback position
                        println("Playback restored: Track Index - $restoredIndex")
                    } else {
                        println("Saved song not found in playlist. Skipping restore.")
                    }
                } else {
                    println("No saved playlist found. Cannot restore.")
                }
            }
            //else {
                //println("Network unavailable. Cannot fetch saved playlist.")
            //}
        }

            /*if (networkStatusProvider.isConnected() && lastPlaylistName != null && musicPlayerKathaVichar.currentMediaItemId.isNotEmpty()) {
                println("gvbdnnb ${musicPlayerKathaVichar.currentMediaItemId}")
                val savedPlaylist = songsDataRepository.fetchSongs(lastPlaylistName)

                val restoredIndex = savedPlaylist.indexOfFirst { it.id == musicPlayerKathaVichar.currentMediaItemId }
                _currentplayingsongs.clear()
                _currentplayingsongs.addAll(savedPlaylist)
                onTrackSelected(restoredIndex)
                musicPlayerKathaVichar.seekToPosition(musicPlayerKathaVichar.currentPlaybackPosition)
                observeMusicPlayerState()
                println("gvbdnnb $restoredIndex")

                *//*val savedPlaylist = songsDataRepository.fetchSongs(lastPlaylistName)

                selectedTrackIndex = savedPlaylist.indexOfFirst { it.id == musicPlayerKathaVichar.currentMediaItemId }
                _currentplayingsongs.clear()
                _currentplayingsongs.addAll(savedPlaylist)
                // musicPlayerKathaVichar.initMusicPlayer(currentplayingsongs.toMediaItemListWithMetadata())
                println("onTrackClicked 1 ")
                println("onTrackClicked 1 fsdg $currentplayingsongs $selectedTrackIndex")*//*
                // onTrackSelected(selectedTrackIndex)

                *//*if (savedPlaylist.isNotEmpty()) {
                    _currentplayingsongs.clear()
                    _currentplayingsongs.addAll(savedPlaylist)
                    updateCurrentPlayingSongs()

                    val songIndex = savedPlaylist.indexOfFirst { it.id == lastSongId }
                    if (songIndex != -1) {
                        selectedTrackIndex = songIndex
                        selectedTrack = savedPlaylist[songIndex]
                        musicPlayerKathaVichar.seekToPosition(lastPosition)
                    }
                }*//*
            } else {
                println("dfgsdfgsdfgsdfg")
            }
*/
        // observeMusicPlayerState()
    }

    /**
     * An immutable snapshot of the current list of tracks.
     */
    val songs: List<Songs> get() = _songs

    private val _currentplayingsongs = mutableStateListOf<Songs>()

    /**
     * An immutable snapshot of the current list of tracks.
     */
    val currentplayingsongs: List<Songs> get() = _currentplayingsongs

    val subscription: CompositeDisposable = CompositeDisposable()

    private val musicPlayerKathaVichar: MusicPlayerKathaVichar by inject(
        MusicPlayerKathaVichar::class.java,
    )

    private var selectedTrackIndex: Int by mutableStateOf(-1)

    var selectedTrack: Songs? by mutableStateOf(null)
        private set

    var whichArtistSelected: String? by mutableStateOf(null)
        private set

    private var isTrackPlay: Boolean = false

    private var isAuto: Boolean = false

    private var playbackStateJob: Job? = null
    private var isBottomClicked: Boolean = false // rename it, based on its purpose

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
    }

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> get() = _searchQuery

    // Convert to StateFlow using MutableStateFlow
    private val _songsFlow = MutableStateFlow<List<Songs>>(emptyList())

    // Update the flow whenever _currentplayingsongs changes
    private fun updateCurrentPlayingSongs() {
        _songsFlow.value = _songs.toList()
    }

    // Combined state for UI
    val filteredSongs: StateFlow<List<Songs>> = combine(
        _searchQuery,
        _songsFlow,
    ) { query, songs ->
        if (query.isEmpty()) {
            _songs
        } else {
            _songs.filter { it.title.contains(query, ignoreCase = true) }
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList(),
    )
    fun onSearchQueryChanged(query: String) {
        _searchQuery.value = query
    }

    private val _isPlayerSetUp = MutableStateFlow(false)
    val isPlayerSetUp = _isPlayerSetUp.asStateFlow()

    fun setupPlayer() {
        _isPlayerSetUp.update {
            true
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun getAllSongs(artistName: String) {
        viewModelScope.launch {
            if (whichArtistSelected == null || whichArtistSelected != artistName) {
                println("dsgs $artistName")
                _uiStateSongs.tryEmit(ServerResponse.isLoading())
                whichArtistSelected = artistName
                songsDataRepository.fetchSongs(artistName).let {
                    val songList =
                        it.map { rawSong ->
                            Songs
                                .Builder()
                                .songId(rawSong.id)
                                .title(rawSong.title)
                                .audioUrl(rawSong.audiourl)
                                .imgUrl(rawSong.imgurl)
                                .artistId(rawSong.artist_id)
                                .build()
                        }
                    // println("argts ${it.get(0).audiourl} ${Uri.parse(it.get(0).audiourl)}")
                    println("argts $it")

                    // Add to _songs and initialize the music player
                    if (songList.isNotEmpty()) {
                        if (currentplayingsongs.isNotEmpty() && selectedTrackIndex != -1) {
                            if (artistName == currentplayingsongs[selectedTrackIndex].artist_id) {
                                println("opopop same artist  $selectedTrackIndex ")
                            /* _currentplayingsongs.clear()
                             _currentplayingsongs.addAll(songs)*/
                                _songs.clear()
                                _songs.addAll(currentplayingsongs)
                                updateCurrentPlayingSongs()
                                // println("opopop same artist  $currentplayingsongs ")

                            /*_currentplayingsongs[selectedTrackIndex].isSelected = true
                            selectedTrack = currentplayingsongs[selectedTrackIndex]*/
                            } else {
                                println("opopop diffe artist  $selectedTrackIndex")
                                // _currentplayingsongs.resetTracks()
                                _songs.clear()
                                _songs.addAll(songList)
                                updateCurrentPlayingSongs()
                                Log.i("edfgwegf", songs.toMediaItemListWithMetadata().toString())
                                _uiStateSongs.tryEmit(ServerResponse.onSuccess(songList.toMutableList()))
                            }
                        } else {
                            _songs.clear()
                            _songs.addAll(songList)
                            Log.i("edfgwegf jhjh", songs.toMediaItemListWithMetadata().toString())
                            updateCurrentPlayingSongs()
                            _uiStateSongs.tryEmit(ServerResponse.onSuccess(songList.toMutableList()))
                        }

                        println("dghyjghfjdg")
                        _searchQuery.value = ""
                        _uiStateSongs.tryEmit(ServerResponse.onSuccess(songList.toMutableList()))
                        observeMusicPlayerState()
                    }
                }
            }
        }
    }

    override fun onPlayPauseClicked(song: Songs?) {
        musicPlayerKathaVichar.playPause()
        println("qrfeqw $song $currentplayingsongs")
    }

    override fun onPreviousClicked(isBottomClick: Boolean, song: Songs?) {
        // if (selectedTrackIndex > 0) onTrackSelected(selectedTrackIndex - 1)
        println("fhjkljjlk $isBottomClick")
        if (isBottomClick && song != null) {
            val currentSongIndex = currentplayingsongs.indexOf(song)
            val currentPlayingSongArtist = currentplayingsongs[currentSongIndex].artist_id

            if (currentPlayingSongArtist == whichArtistSelected) {
                if (currentSongIndex > 0) onTrackSelected(
                    selectedTrackIndex - 1,
                    true,
                )
            } else {
                isBottomClicked = true
                if (currentSongIndex > 0) onTrackSelected(
                    selectedTrackIndex - 1,
                    true,
                )
            }
        }
    }

    override fun onNextClicked(isBottomClick: Boolean, song: Songs?) {
        if (isBottomClick && song != null) {
            val currentSongIndex = currentplayingsongs.indexOf(song)
            val currentPlayingSongArtist = currentplayingsongs[currentSongIndex].artist_id

            if (currentPlayingSongArtist == whichArtistSelected) {
                if (currentSongIndex < currentplayingsongs.size - 1) {
                    onTrackSelected(currentplayingsongs.indexOf(song) + 1, true)
                }
            } else {
                if (currentSongIndex < currentplayingsongs.size - 1) {
                    isBottomClicked = true
                    onTrackSelected(currentplayingsongs.indexOf(song) + 1, true)
                }
            }
        } else {
            val nextSongIndex = selectedTrackIndex + 1
            if (nextSongIndex < currentplayingsongs.size) {
                onTrackSelected(nextSongIndex, true)
            }
        }
    }

    override fun onTrackClicked(song: Songs) {
        // this logic checks on each song tap if the there is any current song
        // playing or not is no song is playing then it will create a currentplayingsongs list
        // Moreover, in else condition it will check if current tapped song exist in previously
        // created currentplayingsongs list. If exist then it means user is on the same screen from same playing
        // list OR else it will updated currentPlayingSongsList and update the UI.

        isBottomClicked = false
        println("sdftghfgsdh ${song.audiourl}")

        try {
            // setupPlayer()
            if (currentplayingsongs.isEmpty()) {
                _currentplayingsongs.clear()
                _currentplayingsongs.addAll(songs)
                println("onTrackClicked $currentplayingsongs}")
                musicPlayerKathaVichar.initMusicPlayer(currentplayingsongs.toMediaItemListWithMetadata())
                onTrackSelected(currentplayingsongs.indexOf(song), true)
            } else {
                /* println("wwewew $selectedTrackIndex")
            val songIndex = songs.indexOf(song)
            println("wwewew nn $selectedTrackIndex")
            if (songIndex == -1) {
                _currentplayingsongs.clear()
                _currentplayingsongs.addAll(songs)
                musicPlayerKathaVichar.initMusicPlayer(currentplayingsongs.toMediaItemListWithMetadata())
                println("onTrackClicked 1 $song ${currentplayingsongs.indexOf(song)}")
                println("onTrackClicked 1 $currentplayingsongs")
                onTrackSelected(currentplayingsongs.indexOf(song))
            } else {
                println("onTrackClicked} 2")
                onTrackSelected(currentplayingsongs.indexOf(song))
            }*/
                // println("fgfgg $songIndex")
                // onTrackSelected(currentplayingsongs.indexOf(song), isTrackClicked = true)

                if (currentplayingsongs.indexOf(song) == -1) {
                    println("artist change $songs")
                    selectedTrackIndex = currentplayingsongs.indexOf(song)
                    _currentplayingsongs.clear()
                    _currentplayingsongs.addAll(songs)
                    musicPlayerKathaVichar.initMusicPlayer(currentplayingsongs.toMediaItemListWithMetadata())
                    println("onTrackClicked 1 $song ${currentplayingsongs.indexOf(song)}")
                    println("onTrackClicked 1 $currentplayingsongs")
                    onTrackSelected(currentplayingsongs.indexOf(song), true)
                } else {
                    onTrackSelected(currentplayingsongs.indexOf(song), true)
                }

                /* onTrackSelected(currentplayingsongs.indexOf(song))
            println("sdafg")*/
            }
        } catch (e: Exception) {
            println("dghsdhdfsg $e")
        }
        // selectedTrackIndex = currentplayingsongs.indexOf(song)
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

    private fun onTrackSelected(index: Int, isSongRestored: Boolean? = false, lastPosition: Long? = 0L) {
        println("ghfrghdfjjj $index")
        if (selectedTrackIndex == -1 || selectedTrackIndex != index) {
            isTrackPlay = true
            selectedTrackIndex = index
            _currentplayingsongs.resetTracks()
            if (!isBottomClicked) {
                println("gygygygy")
                _currentplayingsongs[selectedTrackIndex].isSelected = true
                selectedTrack = currentplayingsongs[selectedTrackIndex]
            }
            if (lastPosition != null) {
                setUpTrack(isSongRestored, lastPosition)
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

    private fun MutableList<Songs>.resetTracks() {
        this.forEach { track ->
            track.isSelected = false
            track.state = MusicPlayerStates.STATE_IDLE
        }
    }

    private fun setUpTrack(isSongRestored: Boolean?, lastPosition: Long) {
        if (!isAuto) musicPlayerKathaVichar.setUpTrack(selectedTrackIndex, isTrackPlay, isSongRestored, lastPosition)
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
        if (selectedTrackIndex != -1) {
            println("rgthger $state")
            isTrackPlay = state == MusicPlayerStates.STATE_PLAYING
            _currentplayingsongs[selectedTrackIndex].state = state
            _currentplayingsongs[selectedTrackIndex].isSelected = true
            selectedTrack = null
            selectedTrack = currentplayingsongs[selectedTrackIndex]

            updatePlaybackState(state)
            if (state == MusicPlayerStates.STATE_NEXT_TRACK) {
                //  isAuto = true
                println("rtyrt7ryurf")
                onNextClicked()
            }
            if (state == MusicPlayerStates.STATE_TRACK_CHANGED) {
                println("dfdfddfdf dvdf $selectedTrackIndex")
                // TODO: check if the changes track is currentlyplaying artist or a new artist song is selected.
                updateSelectedTrackIndex()
            }
            if (state == MusicPlayerStates.STATE_END) onTrackSelected(0, true)
        }
    }

    private fun updateSelectedTrackIndex() {
        val currentMediaItem = musicPlayerKathaVichar.currentMediaItemId
        val index = currentplayingsongs.toMediaItemListWithMetadata().indexOfFirst { currentplayingson ->
            currentplayingson.mediaId == currentMediaItem
        }
        println("yutuyrytry $currentMediaItem $index")
        if (index != -1) {
            onTrackSelected(index, true)
        }
    }

    private fun updatePlaybackState(state: MusicPlayerStates) {
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
                    println("sdfsggf ")
                    delay(1000)
                } while (state == MusicPlayerStates.STATE_PLAYING && isActive)
            }
    }

    fun List<Songs>.toMediaItemListWithMetadata(): MutableList<MediaItem> =
        this
            .map { song ->
                MediaItem
                    .Builder()
                    .setUri(song.audiourl)
                    .setMediaId(song.id)
                    .setMediaMetadata(
                        MediaMetadata
                            .Builder()
                            .setTitle(song.title)
                            .setArtist(song.artist_id)
                            .build(),
                    ).build()
            }.toMutableList()

    override fun onCleared() {
        super.onCleared()
        println("kjkhlkj")
        //  musicPlayerKathaVichar.releasePlayer()
    }
}
