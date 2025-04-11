package com.example.kathavichar.model

import com.example.kathavichar.repositories.musicPla.MusicPlayerKathaVichar
import com.example.kathavichar.repositories.musicPlayer.MusicPlayerStates

data class CurrentMusicItemRestored(
    val duration: Long?,
    val currentPositionItem: Long?,
    val songId: String?,
    val title: String?,
    val artistId: String?,
    val imgUrl: String?,
    val audioUrl: String?,
    var state: MusicPlayerStates?,
    val currentIndex: Int?

)

fun getMusicPlayerState(musicPlayerKathaVichar: MusicPlayerKathaVichar, state: MusicPlayerStates? = null): CurrentMusicItemRestored? {
    val mediaController = musicPlayerKathaVichar.mediaController
    val currentPosition = mediaController?.currentPosition
    val songId = mediaController?.currentMediaItem?.mediaId
    val title = mediaController?.currentMediaItem?.mediaMetadata?.title?.toString()
    val artistId = mediaController?.currentMediaItem?.mediaMetadata?.artist
    val imgUrl = mediaController?.currentMediaItem?.mediaMetadata?.artworkUri
    val audioUrl = mediaController?.currentMediaItem?.localConfiguration?.uri
    val currentIndex = mediaController?.currentMediaItemIndex
    val duration = mediaController?.duration
    return if (songId != null && title != null
                && artistId != null
                && imgUrl != null
                && audioUrl != null && currentPosition != null
    ) {
        CurrentMusicItemRestored(
            currentPositionItem = currentPosition,
            songId = songId,
            title = title,
            artistId = artistId.toString(),
            imgUrl = imgUrl.toString(),
            audioUrl = audioUrl.toString(),
            state = state,
            currentIndex = currentIndex,
            duration = duration,
        )
    } else {
        null
    }
}
