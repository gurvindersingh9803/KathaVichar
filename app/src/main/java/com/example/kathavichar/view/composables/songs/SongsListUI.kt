package com.example.kathavichar.view.composables.songs

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material3.Card
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.rememberLottieComposition
import com.example.kathavichar.R
import com.example.kathavichar.model.Song
import com.example.kathavichar.repositories.musicPlayer.MusicPlayerStates
import com.example.kathavichar.viewModel.SongsViewModel

@Composable
fun SongsListUI(
    songsViewModel: SongsViewModel,
    navigationController: NavHostController,
    modifier: Modifier,
) {
    val lazyListState = rememberLazyListState()
    var scrolledY = 0f
    var previousOffset = 0
    LazyColumn(
        modifier = modifier,
        contentPadding = PaddingValues(5.dp),
        content = {
                /* item {
                Image(
                    painter = painterResource(id = R.drawable.imag),
                    contentDescription = null,
                    contentScale = ContentScale.FillWidth,
                    modifier = Modifier
                        .graphicsLayer {
                            scrolledY += lazyListState.firstVisibleItemScrollOffset - previousOffset
                            translationY = scrolledY * 0.1f
                            previousOffset = lazyListState.firstVisibleItemScrollOffset
                        }
                        .height(200.dp)
                        .fillMaxWidth(),
                )
                Spacer(modifier = Modifier.height(10.dp))
            }*/
            println("dghdr ${songsViewModel.songs[0]}")
            songsViewModel.songs.size?.let {
                items(it) { currentSongIndex ->
                    SongItem(songsViewModel, songsViewModel.songs[currentSongIndex], navigationController)
                }
            }
        },
    )
}

@Composable
fun SongItem(
    songsViewModel: SongsViewModel,
    song: Song,
    navigationController: NavHostController,
) {
    Card(
        modifier =
            Modifier.clickable {
                println("ergfegew $song")
                songsViewModel.onTrackClicked(song)
            },
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceEvenly,
            modifier = Modifier.fillMaxWidth(),
        ) {
            Box(modifier = Modifier.size(50.dp)) {
                Image(
                    painter = painterResource(id = R.drawable.headset),
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                )
            }
            Column {
                Text(
                    text = song.title.toString(),
                    style = MaterialTheme.typography.subtitle1,
                )
                // Text(text = "Track name", style = MaterialTheme.typography.h1)
            }
            Text(text = "2:35", style = MaterialTheme.typography.body2)

            Log.i("ergfegew", song.toString())
            if (song?.state == MusicPlayerStates.STATE_BUFFERING ||
                song?.state == MusicPlayerStates.STATE_PLAYING
            ) {
                LottieAnimationForPlayingSong()
            }
        }
    }

    Spacer(modifier = Modifier.height(20.dp))
}

@Composable
fun LottieAnimationForPlayingSong() {
    val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.audio_wave))
    LottieAnimation(
        composition = composition,
        iterations = Int.MAX_VALUE,
        modifier = Modifier.size(64.dp),
    )
}
