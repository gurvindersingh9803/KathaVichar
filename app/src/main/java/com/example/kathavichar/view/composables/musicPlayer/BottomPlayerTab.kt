package com.example.kathavichar.view.composables.musicPlayer

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.unit.dp
import com.example.kathavichar.common.sharedComposables.NextIcon
import com.example.kathavichar.common.sharedComposables.PausePlayIcon
import com.example.kathavichar.common.sharedComposables.PreviousIcon
import com.example.kathavichar.common.sharedComposables.SongImage
import com.example.kathavichar.common.sharedComposables.SongName
import com.example.kathavichar.model.Songs
import com.example.kathavichar.repositories.musicPlayer.MusicPlayerEvents
import com.example.kathavichar.view.composables.songs.md_theme_light_primary

@Composable
fun BottomPlayerTab(
    song: Songs,
    musicPlayerEvents: MusicPlayerEvents,
    onBottomTabClick: () -> Unit,
) {
    Column(
        Modifier.clickable { onBottomTabClick.invoke() }
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        md_theme_light_primary.copy(alpha = 1f),
                        md_theme_light_primary.copy(alpha = 0.7f)
                    )
                ),
                shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)
            )
            .padding(vertical = 4.dp)
    ) {
        Row(
            modifier =
            Modifier
                .clip(RoundedCornerShape(12.dp))
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            SongImage(songImage = song.imgurl.toString(), modifier = Modifier.size(70.dp))
            SongName(songName = song.title.toString(), modifier = Modifier.weight(1f))
            PreviousIcon(onClick = { musicPlayerEvents.onPreviousClicked(true, song) }, isBottomTab = true)
            PausePlayIcon(
                currentSong = song,
                onClick = {musicPlayerEvents.onPlayPauseClicked(song)},
                isBottomTab = true,
            )
            NextIcon(onClick = { musicPlayerEvents.onNextClicked(true, song) }, isBottomTab = true)
        }
    }
}
