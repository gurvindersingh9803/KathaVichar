package com.example.kathavichar.view.composables.songs

import android.util.Log
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
import com.example.kathavichar.model.Song

@Composable
fun SongsListUI(data: List<Song>?, navigationController: NavHostController) {
    val lazyListState = rememberLazyListState()
    var scrolledY = 0f
    var previousOffset = 0

    Log.i("edfgrsdfd", data.toString())

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
                        SongItem(data[currentSongIndex], navigationController)
                    }
                }
            },
        )
    }
}

@Composable
fun SongItem(songItem: Song?, navigationController: NavHostController) {
    Column() {
        Card(modifier = Modifier.clickable { navigationController.navigate("MusicPlayer") }) {
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