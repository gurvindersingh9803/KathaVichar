package com.example.kathavichar.view.composables.musicPlayer

interface MusicPlayerEvents {

    /**
     * Invoked when the play or pause button is clicked.
     */
    fun onPlayPauseClick()

    /**
     * Invoked when the previous button is clicked.
     */
    fun onPreviousClick()

    /**
     * Invoked when the next button is clicked.
     */
    fun onNextClick()

    /**
     * Invoked when a track is clicked. The clicked [Track] is provided as a parameter.
     *
     * @param track The track that was clicked.
     */
    fun onTrackClick(track: Track)

    /**
     * Invoked when the position of the seek bar has changed. The new position is provided as a parameter.
     *
     * @param position The new position of the seek bar.
     */
    fun onSeekBarPositionChanged(position: Long)
}


/**
 * Represents a single music track.
 *
 * @property trackId The unique identifier for the track.
 * @property trackName The name of the track.
 * @property trackUrl The URL of the track.
 * @property trackImage The resource identifier of the track's image.
 * @property artistName The name of the artist of the track.
 * @property isSelected Indicates if the track is currently selected.
 * @property state The current playback state of the track.
 */
data class Track(
    val trackId: Int = 0,
    val trackName: String = "",
    val trackUrl: String = "",
    val trackImage: Int = 0,
    val artistName: String = "",
    var isSelected: Boolean = false,
    var state: MusicPlayerStates = MusicPlayerStates.STATE_IDLE
) {
    /**
     * Builder class for [Track].
     *
     * This allows for the incremental construction of a [Track] object.
     */
    class Builder {
        private var trackId: Int = 0
        private lateinit var trackName: String
        private lateinit var trackUrl: String
        private var trackImage: Int = 0
        private lateinit var artistName: String
        private var isSelected: Boolean = false
        private var state: MusicPlayerStates = MusicPlayerStates.STATE_IDLE

        fun trackId(trackId: Int) = apply { this.trackId = trackId }
        fun trackName(trackName: String) = apply { this.trackName = trackName }
       //  fun trackUrl(trackUrl: String) = apply { this.trackUrl = BuildConfig.BASE_URL + trackUrl }
        fun trackImage(trackImage: Int) = apply { this.trackImage = trackImage }
        fun artistName(artistName: String) = apply { this.artistName = artistName }

        /**
         * Builds and returns a [Track] object.
         *
         * @return A [Track] object with the set properties.
         */
        fun build(): Track {
            return Track(
                trackId,
                trackName,
                trackUrl,
                trackImage,
                artistName,
                isSelected,
                state
            )
        }
    }
}

/**
 * Data class that represents the current playback state of a media item.
 *
 * @property currentPlaybackPosition Current position in the media item that's currently playing, in milliseconds.
 * @property currentTrackDuration Duration of the current track that's playing, in milliseconds.
 */
data class PlaybackState(
    val currentPlaybackPosition: Long,
    val currentTrackDuration: Long
)