package com.example.kathavichar.view.composables.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.example.kathavichar.common.Screen
import com.example.kathavichar.model.ArtistsItem

/*@Composable
fun HomeCategories(data: List<ArtistsItem>?, navigationController: NavHostController) {
    Column {
        data?.forEach { sectionData ->
            val imageUrl = sectionData.imgurl.replace("127.0.0.1", "10.0.2.2")

            println("dfgthdfgh $imageUrl")
            // Text(sectionData.sectionName, fontSize = 20.sp, modifier = Modifier.padding(8.dp))
            Spacer(modifier = Modifier.height(15.dp))
            Column {
                LazyRow(content = {
                    *//*items(sectionData.data) {
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            PlayListItem(sectionData.data[it], navigationController)
                        }
                    }*//*

                    items(data.size) { index ->
                        println("sdgrfed $index")
                        PlayListItem(sectionData, navigationController, imageUrl)
                    }
                })
            }
            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}*/

@Composable
fun HomeCategories(data: List<ArtistsItem>?, navigationController: NavHostController) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp)
    ) {
        data?.let { artists ->
            LazyVerticalGrid(
                columns = GridCells.Fixed(2), // 2 items per row
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxHeight() // Adjust height as needed
            ) {
                items(artists.size) { index ->
                    val sectionData = artists[index]
                    val imageUrl = sectionData.imgurl.replace("127.0.0.1", "10.0.2.2")
                    PlayListItem(sectionData, navigationController, imageUrl)
                }
            }
        }
    }
}

@Composable
fun PlayListItem(
    sectionItem: ArtistsItem,
    navigationController: NavHostController,
    imageUrl: String
) {
    Card(
        elevation = 4.dp,
        shape = RoundedCornerShape(10.dp),
        modifier = Modifier
            .size(400.dp, 400.dp)
            .padding(3.dp)
            .clickable { navigationController.navigate("${Screen.SongsList.route}/${sectionItem.name}") },
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(102.dp),
            modifier = Modifier.height(IntrinsicSize.Max),
        ) {
            Box() {
                AsyncImage(
                    model = imageUrl,
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxHeight()
                        .aspectRatio(1f), // This ensures a square aspect ratio
                )
            }
            Box(
                modifier = Modifier
                    .background(color = Color(0Xffff9f64)),
            ) {
                Text(
                    sectionItem.name.toString(),
                    fontSize = 20.sp,
                    color = Color.White.copy(alpha = 0.5f),
                    modifier = Modifier.align(Alignment.BottomEnd),
                )
            }
        }
    }
}
