package com.example.kathavichar.common.sharedComposables

import android.graphics.drawable.Icon
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.example.kathavichar.R
import com.example.kathavichar.model.Song
import com.example.kathavichar.repositories.musicPlayer.MusicPlayerStates

@Composable
fun PausePlayIcon(
    currentSong: Song,
    onClick: () -> Unit,
    isBottomTab: Boolean,
) {
    println("efgdews ${currentSong.state}")
    IconButton(onClick = onClick) {
        Icon(
            painter =
                painterResource(
                    id =
                        if (currentSong.state == MusicPlayerStates.STATE_BUFFERING) {
                            R.drawable.baseline_pause_circle_outline_24
                        } else {
                            R.drawable.outline_play_circle_24
                        },
                ),
            contentDescription = "play/pause",
            modifier = Modifier.size(48.dp),
        )
    }
}

@Composable
fun SongName(
    songName: String,
    modifier: Modifier,
) {
    Text(
        text = songName,
        modifier = modifier.padding(start = 16.dp, end = 8.dp),
    )
}

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun SongImage(
    songImage: String,
    modifier: Modifier,
) {
    GlideImage(
        model = songImage,
        contentScale = ContentScale.Crop,
        contentDescription = "track string",
        modifier = modifier.clip(shape = RoundedCornerShape(8.dp)),
    )
}

@Composable
fun PreviousIcon(
    onClick: () -> Unit,
    isBottomTab: Boolean,
) {
    IconButton(onClick = onClick) {
        Icon(
            painter = painterResource(id = R.drawable.baseline_skip_previous_24),
            contentDescription = "previous",
            modifier = Modifier.size(48.dp),
        )
    }
}

@Composable
fun NextIcon(
    onClick: () -> Unit,
    isBottomTab: Boolean,
) {
    IconButton(onClick = onClick) {
        Icon(
            painter = painterResource(id = R.drawable.baseline_skip_next_24),
            contentDescription = "previous",
            modifier = Modifier.size(48.dp),
        )
    }
}
