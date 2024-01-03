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

@Composable
fun BottomPlayerTab(song: Song) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(Color.LightGray)
            .clickable { }
            .padding(15.dp),
    ) {
        Row() {
            SongImage(songImage = song.imgUrl.toString(), modifier = Modifier.size(70.dp))
            SongName(songName = song.title.toString(), modifier = Modifier.weight(1f))
            PreviousIcon(onClick = { }, isBottomTab = true)
            PausePlayIcon(
                currentSong = song,
                onClick = {},
                isBottomTab = true,
            )
            NextIcon(onClick = {}, isBottomTab = true)
        }
    }
}
