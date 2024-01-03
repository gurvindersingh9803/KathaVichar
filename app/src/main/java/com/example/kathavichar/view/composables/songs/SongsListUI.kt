package com.example.kathavichar.view.composables.songs

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material3.Card
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.kathavichar.R
import com.example.kathavichar.common.Screen
import com.example.kathavichar.model.Song
import com.example.kathavichar.view.composables.musicPlayer.BottomPlayerTab
import com.example.kathavichar.viewModel.SongsViewModel
import com.google.gson.Gson
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

@Composable
fun SongsListUI(data: List<Song>?, songsViewModel: SongsViewModel, navigationController: NavHostController) {
    val lazyListState = rememberLazyListState()
    var scrolledY = 0f
    var previousOffset = 0

    Column(Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
        LazyColumn(
            Modifier.fillMaxSize(),
            lazyListState,
            content = {
                item {
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
                }
                data?.size?.let {
                    items(it) { currentSongIndex ->
                        SongItem(data[currentSongIndex], songsViewModel, navigationController)
                    }
                }
            },
        )
    }
}

@Composable
fun SongItem(
    songItem: Song?,
    songsViewModel: SongsViewModel,
    navigationController: NavHostController
) {
    val gson = Gson() // TODO: Make it only composable 1 time in future.
    val songItemString = gson.toJson(songItem, Song::class.java)
    val encode = URLEncoder.encode(songItemString, StandardCharsets.UTF_8.toString())

    Column() {
        Card(modifier = Modifier.clickable { songItem?.let { songsViewModel.onTrackClicked(it) } }) {
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

                Column() {
                    if (songItem != null) {
                        Text(
                            text = songItem.title.toString(),
                            style = MaterialTheme.typography.subtitle1,
                        )
                    }
                    // Text(text = "Track name", style = MaterialTheme.typography.h1)
                }
                Text(text = "2:35", style = MaterialTheme.typography.body2)
            }
        }

        Spacer(modifier = Modifier.height(20.dp))
    }
}
