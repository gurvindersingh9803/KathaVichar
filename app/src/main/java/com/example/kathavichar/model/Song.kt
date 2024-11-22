package com.example.kathavichar.model

import com.example.kathavichar.repositories.musicPlayer.MusicPlayerStates

/*
data class Song(
    val audioUrl: String,
    val imgUrl: String?,
    val title: String?,
    var state: MusicPlayerStates = MusicPlayerStates.STATE_IDLE,
    var isSelected: Boolean = false
)
*/
data class Song(
    val title: String = "",
    val audioUrl: String = "",
    val imgUrl: String? = "",
    val artistName: String = "",
    var isSelected: Boolean = false,
    var state: MusicPlayerStates = MusicPlayerStates.STATE_IDLE,
) {
    /**
     * Builder class for [Song].
     *
     * This allows for incremental construction of a [Song] object.
     */
    class Builder {
        private var title: String = ""
        private var audioUrl: String = ""
        private var imgUrl: String? = ""
        private var artistName: String = ""
        private var isSelected: Boolean = false
        private var state: MusicPlayerStates = MusicPlayerStates.STATE_IDLE

        fun title(title: String) = apply { this.title = title }

        fun audioUrl(audioUrl: String) = apply { this.audioUrl = audioUrl }

        fun imgUrl(imgUrl: String?) = apply { this.imgUrl = imgUrl }

        fun artistName(artistName: String) = apply { this.artistName = artistName }

        fun isSelected(isSelected: Boolean) = apply { this.isSelected = isSelected }

        fun state(state: MusicPlayerStates) = apply { this.state = state }

        /**
         * Builds and returns a [Song] object with the specified properties.
         *
         * @return A [Song] instance.
         */
        fun build(): Song =
            Song(
                title = title,
                audioUrl = audioUrl,
                imgUrl = imgUrl,
                artistName = artistName,
                isSelected = isSelected,
                state = state,
            )
    }
}
