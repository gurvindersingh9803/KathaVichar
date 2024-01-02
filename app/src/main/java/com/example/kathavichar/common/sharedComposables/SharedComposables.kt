package com.example.kathavichar.common.sharedComposables

import android.graphics.drawable.Icon
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.kathavichar.R
import com.example.kathavichar.model.Song
import com.example.kathavichar.repositories.musicPlayer.MusicPlayerStates

@Composable
fun PausePlayIcon(currentSong: Song, onClick: () -> Unit, isBottomTab: Boolean) {
    if (currentSong.state == MusicPlayerStates.STATE_BUFFERING) {
        CircularProgressIndicator(
            modifier = Modifier
                .size(size = 48.dp)
                .padding(all = 9.dp),
            color = Color.Gray,
        )
    } else {
        IconButton(onClick = onClick) {
            Icon(
                painter = painterResource(
                    id = if (currentSong.state == MusicPlayerStates.STATE_PLAYING) {
                        R.drawable.imag
                    } else {
                        R.drawable.headset
                    },
                ),
                contentDescription = "play/pause",
                modifier = Modifier.size(48.dp),
            )
        }
    }
}

@Composable
fun songName(trackName: String, modifier: Modifier) {
    Text(
        text = trackName,
        modifier = modifier.padding(start = 16.dp, end = 8.dp),
    )
}
