package com.example.kathavichar.view.composables.musicPlayer

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.kathavichar.common.sharedComposables.NextIcon
import com.example.kathavichar.common.sharedComposables.PausePlayIcon
import com.example.kathavichar.common.sharedComposables.PreviousIcon
import com.example.kathavichar.common.sharedComposables.SongImage
import com.example.kathavichar.common.sharedComposables.SongName
import com.example.kathavichar.model.Song
import com.example.kathavichar.repositories.musicPlayer.MusicPlayerEvents
import com.example.kathavichar.view.composables.songs.md_theme_light_primary

@Composable
fun BottomPlayerTab(song: Song, musicPlayerEvents: MusicPlayerEvents, onBottomTabClick: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(md_theme_light_primary)
            .clickable(onClick = onBottomTabClick)
            .padding(15.dp),
    ) {
        Row() {
            SongImage(songImage = song.imgUrl.toString(), modifier = Modifier.size(70.dp))
            SongName(songName = song.title.toString(), modifier = Modifier.weight(1f))
            PreviousIcon(onClick = musicPlayerEvents::onPreviousClicked, isBottomTab = true)
            PausePlayIcon(
                currentSong = song,
                onClick = musicPlayerEvents::onPlayPauseClicked,
                isBottomTab = true,
            )
            NextIcon(onClick = musicPlayerEvents::onNextClicked, isBottomTab = true)
        }
    }
}
