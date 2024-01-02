package com.example.kathavichar.viewModel

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.toMutableStateList
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.common.MediaItem
import com.example.kathavichar.model.Song
import com.example.kathavichar.network.ServerResponse
import com.example.kathavichar.repositories.SongsListFirebase
import com.example.kathavichar.repositories.musicPlayer.MusicPlayerEvents
import com.example.kathavichar.repositories.musicPlayer.MusicPlayerKathaVichar
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
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
        MusicPlayerKathaVichar::class.java
    )

    private var selectedTrackIndex: Int by mutableStateOf(-1)

    private var isTrackPlay: Boolean = false

    private var isAuto: Boolean = false

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
        TODO("Not yet implemented")
    }

    override fun onTrackClicked(song: Song) {
        onTrackSelected(songs.indexOf(song))
    }

    override fun onSeekBarPositionChanged(position: Long) {
        TODO("Not yet implemented")
    }

    fun observeMusicPlayerState() {
        viewModelScope.launch {
            musicPlayerKathaVichar.playerState.collect{
                Log.i("ergftewgtw", it.name)
            }
        }
    }

    private fun onTrackSelected(index: Int) {
        Log.i("fggfrghergth", index.toString())
        if (selectedTrackIndex == -1) isTrackPlay = true
        if (selectedTrackIndex == -1 || selectedTrackIndex != index) {
            // _tracks.resetTracks()
            selectedTrackIndex = index
            setUpTrack()
        }
    }

    private fun setUpTrack() {
        if (!isAuto) musicPlayerKathaVichar.setUpTrack(selectedTrackIndex, isTrackPlay)
        isAuto = false
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
