package com.musicplayer.kathavichar.viewModel

import androidx.lifecycle.ViewModel

class MusicPlayerViewModel : ViewModel() {
    /*    private val musicPlayerKathaVichar: MusicPlayerKathaVichar by inject(MusicPlayerKathaVichar::class.java)

    private var selectedTrackIndex: Int by mutableStateOf(-1)

    private var isTrackPlay: Boolean = false

    private var isAuto: Boolean = false

    private val _songs = mutableStateListOf<Song>()
     */
/**
     * An immutable snapshot of the current list of tracks.
     */
/*
    val songs: List<Song> get() = _songs



 */
/*    private val musicPlayerService: MusicPlayerService by inject(MusicPlayerService::class.java)
        private val compositeDisposable = CompositeDisposable()
        private val schedulerProvider = DefaultSchedulerProvider()*/
/*

    init {
        musicPlayerKathaVichar.initMusicPlayer()
        observeMusicPlayerState()
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
        TODO("Not yet implemented")
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
 */
/* fun playSong(soundFileType: Uri) {
         compositeDisposable.add(
             musicPlayerService.playSound(soundFileType)
                 .subscribeOn(schedulerProvider.io())
                 .observeOn(AndroidSchedulers.mainThread())
                 .subscribe({
                     Log.i("Media player task completed", "Completed")
                     musicPlayerService.pauseMusicPlayer()
                 }, {
                     Log.i("Media player error occurred", it.toString())
                     musicPlayerService.pauseMusicPlayer()
                 }),
         )
     }*/
}
