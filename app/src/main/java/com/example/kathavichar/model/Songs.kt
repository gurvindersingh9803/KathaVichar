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
) {
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

        fun title(title: String) = apply { this.title = title }

        fun audioUrl(audioUrl: String) = apply { this.audiourl = audioUrl }

        fun imgUrl(imgUrl: String) = apply { this.imgurl = imgUrl }

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
            )
    }
}
