package com.example.kathavichar.viewModel

import android.app.Activity
import android.content.Context
import android.media.MediaMetadataRetriever
import android.media.session.PlaybackState
import android.net.Uri
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import com.example.kathavichar.common.SharedPrefsManager
import com.example.kathavichar.common.utils.PlayTimeTracker
import com.example.kathavichar.model.Songs
import com.example.kathavichar.model.getMusicPlayerState
import com.example.kathavichar.network.ServerResponse
import com.example.kathavichar.repositories.SongsDataRepository
import com.example.kathavichar.repositories.musicPla.MusicPlayerKathaVichar
import com.example.kathavichar.repositories.musicPlayer.MusicPlayerEvents
import com.example.kathavichar.repositories.musicPlayer.MusicPlayerStates
import com.example.kathavichar.repositories.musicPlayer.PlayerBackState
import com.example.kathavichar.view.composables.musicPlayer.AdManager
import io.reactivex.disposables.CompositeDisposable
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
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
import kotlinx.coroutines.withContext
import org.koin.java.KoinJavaComponent
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

    private val _shouldStartAdCountTiming = MutableLiveData<Boolean>()
    val shouldStartAdCountTiming: LiveData<Boolean> = _shouldStartAdCountTiming



    // TODO: stop this function to call unnecessary until restore is required.
    fun restorePlaybackState() {
        observeMusicPlayerState()
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

    val musicPlayerKathaVichar: MusicPlayerKathaVichar by inject(
        MusicPlayerKathaVichar::class.java,
    )

    private var selectedTrackIndex: Int by mutableStateOf(-1)

    var selectedTrack: Songs? by mutableStateOf(null)
        private set

    var whichArtistSelected: String? by mutableStateOf(null)
        private set

    private var isTrackPlay: Boolean = false

    private var getCurrentRestoredItemIndex = -1
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




    @RequiresApi(Build.VERSION_CODES.O)
    fun getAllSongs(artistName: String) {
        viewModelScope.launch {
            if (whichArtistSelected == null || whichArtistSelected != artistName) {
                println("dsgs $artistName $getCurrentRestoredItemIndex ")
                _uiStateSongs.tryEmit(ServerResponse.isLoading())
                whichArtistSelected = artistName
                println("kljhjkukl rrf $artistName ${selectedTrack?.artist_id} $getCurrentRestoredItemIndex")
                val songList = withContext(Dispatchers.IO) {
                    songsDataRepository.fetchSongs(artistName).map { rawSong ->
                        val duration = try {
                            MediaMetadataRetriever().use { retriever ->
                                println("Fetching duration for: ${rawSong.audiourl}")
                                retriever.setDataSource(
                                    rawSong.audiourl,
                                    mapOf("User-Agent" to "Kathavichar/1.0"),
                                )
                                val durationStr =
                                    retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)
                                println("Raw duration for ${rawSong.title}: $durationStr")
                                durationStr?.toLongOrNull() ?: 0L.also {
                                    if (durationStr == null) println("No duration metadata for ${rawSong.title}")
                                }
                            }
                        } catch (e: Exception) {
                            println("Error fetching duration for ${rawSong.title} (${rawSong.audiourl}): ${e.javaClass.simpleName} - ${e.message}")
                            0L
                        }
                        println("Building song ${rawSong.title} with duration: $duration")
                        val song = Songs.Builder()
                            .songId(rawSong.id)
                            .title(rawSong.title)
                            .audioUrl(rawSong.audiourl)
                            .imgUrl(rawSong.imgurl)
                            .artistId(rawSong.artist_id)
                            .duration(duration)
                            .build()
                        println("Built song: $song")
                        song
                    }
                }

                println("Song list with durations: $songList")
                /*if(artistName == selectedTrack?.artist_id && getCurrentRestoredItemIndex != -1) {
                    selectedTrack = currentplayingsongs[getCurrentRestoredItemIndex]
                }*/
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
                            // restorePlaybackStateIfNeeded().
                            _uiStateSongs.tryEmit(ServerResponse.onSuccess(songList.toMutableList()))

                            /*_currentplayingsongs[selectedTrackIndex].isSelected = true
                            selectedTrack = currentplayingsongs[selectedTrackIndex]*/
                        } else {
                            println("opopop diffe artist  $selectedTrackIndex")
                            // _currentplayingsongs.resetTracks()
                            _songs.clear()
                            _songs.addAll(songList)
                            updateCurrentPlayingSongs()
                            Log.i("edfgwegf", selectedTrackIndex.toString())
                            _uiStateSongs.tryEmit(ServerResponse.onSuccess(songList.toMutableList()))
                        }
                    } else {
                        // restorePlaybackStateIfNeeded(MusicPlayerStates.STATE_PLAYING, songList).let {
                        _songs.clear()
                        _songs.addAll(songList)
                        updateCurrentPlayingSongs()
                        _uiStateSongs.tryEmit(ServerResponse.onSuccess(songList.toMutableList())).also {
                            if (isPlaybackRestored) {
                                getMusicPlayerState(musicPlayerKathaVichar).let { currentMediaControllerItem ->
                                    println("sdefrgdfg $currentMediaControllerItem")

                                    if (currentMediaControllerItem != null) {
                                        selectedTrack = Songs(
                                            artist_id = currentMediaControllerItem.artistId.toString(),
                                            audiourl = currentMediaControllerItem.audioUrl.toString(),
                                            id = currentMediaControllerItem.songId.toString(),
                                            imgurl = currentMediaControllerItem.imgUrl.toString(),
                                            title = currentMediaControllerItem.title.toString(),
                                            state = MusicPlayerStates.STATE_IDLE,
                                            isSelected = false,
                                            duration = currentMediaControllerItem.duration,
                                        )
                                        println("khghjghjghj 1 $selectedTrack $songList")
                                        onTrackClicked(selectedTrack!!)
                                    }
                                }
                            }
                        }
                        // }
                    }

                    println("dghyjghfjdg")
                    _searchQuery.value = ""
                    // _uiStateSongs.tryEmit(ServerResponse.onSuccess(songList.toMutableList()))*/
                    observeMusicPlayerState()
                }
            } else {
            }
        }
    }

    override fun onPlayPauseClicked(song: Songs?) {
        musicPlayerKathaVichar.playPause()
        println("qrfeqw $song $currentplayingsongs")
    }

    override fun onPreviousClicked(isBottomClick: Boolean, song: Songs?) {
        // if (selectedTrackIndex > 0) onTrackSelected(selectedTrackIndex - 1)
        // TODO: crashing when clicking back song in restored state
        // TODO: try to test isPlaybackRestored as much as possible.
        //  Break isPlayBackRestored Method in both NEXT SONG CHANGE STATE and PREVIOUS SONG STATE.

        println("gfn jkhjkh $isPlaybackRestored $selectedTrackIndex")
       if (isPlaybackRestored) {
            val mediaController = musicPlayerKathaVichar.mediaController
            if (mediaController != null) {
                if (mediaController.hasPreviousMediaItem()) {
                    mediaController.seekToPrevious()
                    if (song != null) {
                        getMusicPlayerState(musicPlayerKathaVichar).let { currentMediaControllerItem ->
                            if (currentMediaControllerItem != null && song != null) {
                                println("khghjghjghj ${song.state}")
                                /*selectedTrack = Songs(
                                    artist_id = currentMediaControllerItem.artistId.toString(),
                                    audiourl = currentMediaControllerItem.audioUrl.toString(),
                                    id = currentMediaControllerItem.songId.toString(),
                                    imgurl = currentMediaControllerItem.imgUrl.toString(),
                                    title = currentMediaControllerItem.title.toString(),
                                    state = MusicPlayerStates.STATE_IDLE,
                                    isSelected = false,
                                    duration = currentMediaControllerItem.duration,
                                )*/
                                onTrackClicked(selectedTrack!!)
                                println("khghjghjghj 1 $selectedTrack")

                            }
                            // restorePlaybackStateIfNeeded(song.state)
                        }
                    }
                }
            }
           return
       }

        println("fhjkljjlk $isBottomClick")
        if (isBottomClick && song != null) {
            val currentSongIndex = currentplayingsongs.indexOf(song)
            val currentPlayingSongArtist = currentplayingsongs[currentSongIndex].artist_id

            if (currentPlayingSongArtist == whichArtistSelected) {
                println("fhjkljjlk sub $selectedTrackIndex")
                if (currentSongIndex > 0) {
                    onTrackSelected(
                        selectedTrackIndex - 1,
                        true,
                    )
                }
            } else {
                isBottomClicked = true
                if (currentSongIndex > 0) {
                    println("gfn jkhjkh second $currentSongIndex $selectedTrackIndex")
                    onTrackSelected(
                        selectedTrackIndex - 1,
                        true,
                    )
                }
            }
        }
        println("gfn jkhjkh 2 $isPlaybackRestored $selectedTrackIndex")
    }

    override fun onNextClicked(isBottomClick: Boolean, song: Songs?) {
        if (isPlaybackRestored) {
            val mediaController = musicPlayerKathaVichar.mediaController
            if (mediaController != null) {
                if (mediaController.hasNextMediaItem()) {
                    mediaController.seekToNext()
                    if (song != null) {
                        getMusicPlayerState(musicPlayerKathaVichar).let { currentMediaControllerItem ->
                            if (currentMediaControllerItem != null) {
                                // selectedTrackIndex = currentMediaControllerItem.currentIndex!!
                               /* println("dsfagdsfg $selectedTrackIndex")
                                selectedTrack = Songs(
                                    artist_id = currentMediaControllerItem.artistId.toString(),
                                    audiourl = currentMediaControllerItem.audioUrl.toString(),
                                    id = currentMediaControllerItem.songId.toString(),
                                    imgurl = currentMediaControllerItem.imgUrl.toString(),
                                    title = currentMediaControllerItem.title.toString(),
                                    state = MusicPlayerStates.STATE_IDLE,
                                    isSelected = false,
                                    duration = currentMediaControllerItem.duration,
                                )*/
                                onTrackClicked(selectedTrack!!)
                            }
                        }
                        return
                    }
                   /* getMusicPlayerState(musicPlayerKathaVichar).let { currentMediaControllerItem ->
                        if (currentMediaControllerItem != null && song != null) {
                            println("khghjghjghj ${song.state}")
                            getCurrentRestoredItemIndex = currentMediaControllerItem.currentIndex!!
                            selectedTrack = Songs(
                                artist_id = currentMediaControllerItem.artistId.toString(),
                                audiourl = currentMediaControllerItem.audioUrl.toString(),
                                id = currentMediaControllerItem.songId.toString(),
                                imgurl = currentMediaControllerItem.imgUrl.toString(),
                                title = currentMediaControllerItem.title.toString(),
                                state = song.state,
                                isSelected = true,
                                duration = 0L,
                            )
                            println("khghjghjghj 1 $selectedTrack")
                        }
                    }
                    println("jhjhgkhgkhh ${selectedTrack?.audiourl} ${musicPlayerKathaVichar.mediaController?.currentMediaItem?.localConfiguration?.uri}")*/
                }

               /* if (song != null) {
                    restorePlaybackStateIfNeeded(song.state)
                }*/
            }
        }
        if (isBottomClick == true && song != null && !isPlaybackRestored) {
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

    private fun restorePlaybackStateIfNeeded(
        musicPlayerStates: MusicPlayerStates,
        restoredList: List<Songs>? = null,
    ): List<Songs> {
        val fallbackList = if (restoredList.isNullOrEmpty()) songs else restoredList

        if (isPlaybackRestored && getCurrentRestoredItemIndex != -1 && whichArtistSelected == selectedTrack?.artist_id) {
            return fallbackList.mapIndexed { index, song ->
                println("dsfgwewtrwrewe $index $getCurrentRestoredItemIndex")
                if (index == getCurrentRestoredItemIndex) {
                    song.copy(
                        isSelected = true,
                        state = musicPlayerStates,
                    )
                } else {
                    song.copy(
                        isSelected = false,
                        state = MusicPlayerStates.STATE_IDLE,
                    )
                }
            }.also { updatedList ->
                selectedTrackIndex = getCurrentRestoredItemIndex
                selectedTrack = updatedList[getCurrentRestoredItemIndex]
            }
        }
        return fallbackList
    }
    override fun onTrackClicked(song: Songs) {
        // this logic checks on each song tap if the there is any current song
        // playing or not is no song is playing then it will create a currentplayingsongs list
        // Moreover, in else condition it will check if current tapped song exist in previously
        // created currentplayingsongs list. If exist then it means user is on the same screen from same playing
        // list OR else it will updated currentPlayingSongsList and update the UI.

        isBottomClicked = false
        // isPlaybackRestored = false
       // getCurrentRestoredItemIndex = -1
        try {
            var localIndex: Int = -1
            // setupPlayer()
            if (currentplayingsongs.isEmpty()) {
                _currentplayingsongs.clear()
                _currentplayingsongs.addAll(songs)
                println("onTrackClicked $isPlaybackRestored}")

                if(!isPlaybackRestored) {
                    //selectedTrackIndex = currentplayingsongs.indexOf(song)
                    musicPlayerKathaVichar.initMusicPlayer(currentplayingsongs.toMediaItemListWithMetadata())
                    localIndex = currentplayingsongs.indexOf(song)
                } else {
                    val matchingSongIndex = currentplayingsongs.indexOfFirst { it.id == selectedTrack?.id }
                    localIndex = matchingSongIndex
                    //selectedTrackIndex = matchingSongIndex
                }

                onTrackSelected(localIndex, true)
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

    private fun observeMusicPlayerState(
        artistName: String? = "",
        audioUrl: String? = "",
        songId: String? = "",
        imgUrl: String? = "",
        title: String? = "",
    ) {
        viewModelScope.launch {
            musicPlayerKathaVichar._playerStates.collect { state ->
                println("sdfghdf $state $selectedTrackIndex $isPlaybackRestored")
                updateState(state, artistName, audioUrl, songId, imgUrl, title)

                if(state == MusicPlayerStates.STATE_PLAYING) {
                    _shouldStartAdCountTiming.postValue(true)
                }
            }
        }
    }

    // TODO: remove isSongRestored: Boolean? = false, lastPosition: Long? = 0L
    private fun onTrackSelected(index: Int, isSongRestored: Boolean? = false, lastPosition: Long? = 0L) {
        println("ghfrghdfjjj gygygygy $isPlaybackRestored")
        if (isPlaybackRestored) {
            // isTrackPlay = true
            selectedTrackIndex = index
            _currentplayingsongs.resetTracks()
            _currentplayingsongs[selectedTrackIndex].isSelected = true
            selectedTrack = currentplayingsongs[selectedTrackIndex]
            isPlaybackRestored = false
            return
        }

        if (selectedTrackIndex == -1 || selectedTrackIndex != index) {
            println("gygygygy fdfdf $_currentplayingsongs")
            isTrackPlay = true
            selectedTrackIndex = index
            _currentplayingsongs.resetTracks()
            if (!isBottomClicked) {
                _currentplayingsongs[selectedTrackIndex].isSelected = true
                selectedTrack = currentplayingsongs[selectedTrackIndex]
                setUpTrack()
            }

            setUpTrack()
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

    private fun setUpTrack(isSongRestored: Boolean? = false, lastPosition: Long? = 0L) {
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

    private fun updateState(
        state: MusicPlayerStates,
        artistName: String? = "",
        audioUrl: String? = "",
        songId: String? = "",
        imgUrl: String? = "",
        title: String? = "",
    ) {
        if (selectedTrackIndex != -1 && !isPlaybackRestored) {
            println("rgthger fghfghfsujkll $state")
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
        } else if (isPlaybackRestored) {
            updatePlaybackState(state)
            getMusicPlayerState(musicPlayerKathaVichar, state).let { currentMediaControllerItem ->
                if (currentMediaControllerItem != null) {
                    // getCurrentRestoredItemIndex = currentMediaControllerItem.currentIndex!!
                     selectedTrackIndex = currentMediaControllerItem.currentIndex!!
                    println("hhjjh $selectedTrackIndex")
                    selectedTrack = currentMediaControllerItem.state?.let {
                        Songs(
                            artist_id = currentMediaControllerItem.artistId.toString(),
                            audiourl = currentMediaControllerItem.audioUrl.toString(),
                            id = currentMediaControllerItem.songId.toString(),
                            imgurl = currentMediaControllerItem.imgUrl.toString(),
                            title = currentMediaControllerItem.title.toString(),
                            state = it,
                            isSelected = false,
                            duration = currentMediaControllerItem.duration,
                        )
                    }
                }
            }
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
                            .setArtworkUri(Uri.parse(song.imgurl))
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
