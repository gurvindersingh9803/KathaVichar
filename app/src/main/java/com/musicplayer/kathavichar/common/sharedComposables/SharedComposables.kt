package com.musicplayer.kathavichar.common.sharedComposables

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
import com.musicplayer.kathavichar.R
import com.musicplayer.kathavichar.common.utils.isDataLoading
import com.musicplayer.kathavichar.model.Songs
import com.musicplayer.kathavichar.repositories.musicPlayer.MusicPlayerStates

@Composable
fun PausePlayIcon(
    currentSong: Songs,
    onClick: () -> Unit,
    isBottomTab: Boolean,
) {
    IconButton(onClick = onClick) {
        println("ewrfw $currentSong")
        if (currentSong.state == MusicPlayerStates.STATE_BUFFERING) {
            isDataLoading()
        } else {
            Icon(
                painter =
                painterResource(
                    id =
                    if (currentSong.state == MusicPlayerStates.STATE_PLAYING) {
                        R.drawable.baseline_pause_24
                    } else {
                        R.drawable.baseline_play_arrow_24
                    },
                ),
                contentDescription = "play/pause",
                modifier = Modifier.size(48.dp),
            )
        }
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
        modifier = modifier.clip(shape = RoundedCornerShape(6.dp)),
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
