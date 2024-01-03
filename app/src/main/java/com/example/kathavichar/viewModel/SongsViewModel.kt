package com.example.kathavichar.viewModel

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.common.MediaItem
import com.example.kathavichar.model.PlayBackState
import com.example.kathavichar.model.Song
import com.example.kathavichar.network.ServerResponse
import com.example.kathavichar.repositories.SongsListFirebase
import com.example.kathavichar.repositories.musicPlayer.MusicPlayerEvents
import com.example.kathavichar.repositories.musicPlayer.MusicPlayerKathaVichar
import com.example.kathavichar.repositories.musicPlayer.MusicPlayerStates
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import org.koin.java.KoinJavaComponent

class SongsViewModel : ViewModel(), MusicPlayerEvents {

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

    private val playbackStateFlow: MutableStateFlow<PlayBackState> = MutableStateFlow(PlayBackState(currentPlaybackPosition = musicPlayerKathaVichar.currentPlaybackPosition, currentTrackDuration = musicPlayerKathaVichar.currentTrackDuration))

    init {
        observeMusicPlayerState()
    }

    fun getSongs(artistName: String) {
        viewModelScope.launch {
            subscription.add(
                sonsListFirebase.getSongsList(artistName)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe({
                        viewModelScope.launch {
                            _songs.addAll(it)
                            musicPlayerKathaVichar.initMusicPlayer(songs.toMediaItemList())
                            _uiStateSongs.emit(ServerResponse.onSuccess(it))
                        }
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
        TODO("Not yet implemented")
    }

    override fun onNextClicked() {
        if (selectedTrackIndex < songs.size - 1) onTrackSelected(selectedTrackIndex + 1)
    }

    override fun onTrackClicked(song: Song) {
        onTrackSelected(songs.indexOf(song))
    }

    override fun onSeekBarPositionChanged(position: Long) {
        TODO("Not yet implemented")
    }

    fun observeMusicPlayerState() {
        viewModelScope.launch {
            musicPlayerKathaVichar.playerState.collect {
                Log.i("ergftewgtw", it.name)
                updateState(it)
            }
        }
    }

    private fun onTrackSelected(index: Int) {
        Log.i("fggfrghergth", index.toString())
        if (selectedTrackIndex == -1) isTrackPlay = true
        if (selectedTrackIndex == -1 || selectedTrackIndex != index) {
            selectedTrackIndex = index
            resetTracks()
            setUpTrack()
        }
    }

    private fun resetTracks() {
        _songs.forEach { track ->
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
            isTrackPlay = state == MusicPlayerStates.STATE_PLAYING
            _songs[selectedTrackIndex].state = state
            _songs[selectedTrackIndex].isSelected = true
            selectedTrack = null
            selectedTrack = songs[selectedTrackIndex]

            updatePlaybackState(state)
            if (state == MusicPlayerStates.STATE_NEXT_TRACK) {
                isAuto = true
                onNextClicked()
            }
            if (state == MusicPlayerStates.STATE_END) onTrackSelected(0)
        }
    }

    private fun updatePlaybackState(state: MusicPlayerStates) {
        playbackStateFlowJob.cancel()
        playbackStateFlowJob.launch {
            do {
                playbackStateFlow.emit(
                    PlayBackState(
                        currentPlaybackPosition = musicPlayerKathaVichar.currentPlaybackPosition,
                        currentTrackDuration = musicPlayerKathaVichar.currentTrackDuration,
                    ),
                )
                Log.i("wfeeff", "")
                delay(1000)
            } while (state == MusicPlayerStates.STATE_PLAYING && isActive)
        }
    }

    /**
     * Converts a list of [Track] objects into a mutable list of [MediaItem] objects.
     *
     * @return A mutable list of [MediaItem] objects.
     */
    fun List<Song>.toMediaItemList(): MutableList<MediaItem> {
        return this.map { MediaItem.fromUri(it.audioUrl) }.toMutableList()
    }
}
