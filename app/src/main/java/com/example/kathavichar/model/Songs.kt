package com.example.kathavichar.model

import com.example.kathavichar.repositories.musicPlayer.MusicPlayerStates
data class Songs(
    val artist_id: String,
    val audiourl: String,
    val id: String,
    val imgurl: String,
    val title: String,
    var state: MusicPlayerStates = MusicPlayerStates.STATE_IDLE,
    var isSelected: Boolean = false,
    var duration: Long? = 0L,
) {

    // Human-readable duration (e.g., "03:12" or "01:03:12")
    val formattedDuration: String?
        get() = duration?.let { formatDuration(it) }

    private fun formatDuration(millis: Long): String {
        val totalSeconds = millis / 1000
        val hours = totalSeconds / 3600
        val minutes = (totalSeconds % 3600) / 60
        val seconds = totalSeconds % 60

        return if (hours > 0) {
            String.format("%02d:%02d:%02d", hours, minutes, seconds)
        } else {
            String.format("%02d:%02d", minutes, seconds)
        }
    }

    /**
     * Builder class for [Song].
     *
     * This allows for incremental construction of a [Song] object.
     */
    class Builder {
        private var title: String = ""
        private var audiourl: String = ""
        private var imgurl: String = ""
        private var artist_id: String = ""
        private var isSelected: Boolean = false
        private var state: MusicPlayerStates = MusicPlayerStates.STATE_IDLE
        private var id: String = ""
        private var duration: Long? = 0L

        fun title(title: String) = apply { this.title = title }

        fun audioUrl(audioUrl: String) = apply { this.audiourl = audioUrl }

        fun imgUrl(imgUrl: String) = apply { this.imgurl = imgUrl }

        fun duration(duration: Long?) = apply { this.duration = duration }

        fun artistId(artistName: String) = apply { this.artist_id = artistName }

        fun songId(id: String) = apply { this.id = id }

        fun isSelected(isSelected: Boolean) = apply { this.isSelected = isSelected }

        fun state(state: MusicPlayerStates) = apply { this.state = state }

        /**
         * Builds and returns a [Song] object with the specified properties.
         *
         * @return A [Song] instance.
         */
        fun build(): Songs =
            Songs(
                id = id,
                title = title,
                audiourl = audiourl,
                imgurl = imgurl,
                artist_id = artist_id,
                isSelected = isSelected,
                state = state,
                duration = duration
            )
    }
}
