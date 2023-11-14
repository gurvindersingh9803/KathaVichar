package com.example.kathavichar.view.categories

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material.Card
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.kathavichar.R

@Preview
@Composable
fun PlayList() {
    LazyVerticalGrid(columns = GridCells.Fixed(2), content = {
        items(5) {
            PlayListItem()
        }
    })
}

@Composable
fun PlayListItem() {
    Card(elevation = 4.dp, modifier = Modifier.padding(3.dp)) {
        Column {
            Box(modifier = Modifier.fillMaxSize()) {
                Image(
                    painter = painterResource(id = R.drawable.img),
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.height(200.dp)
                )
            }
            Box(modifier = Modifier.fillMaxSize().background(color = Color.DarkGray)) {
                Text("Sant Ji", fontSize = 30.sp, color = Color.White.copy(alpha = 0.5f), modifier = Modifier.align(Alignment.BottomEnd))
            }
        }
    }
}
