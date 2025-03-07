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
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
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
import com.example.kathavichar.model.ArtistData
import com.example.kathavichar.model.ArtistSummary
import com.example.kathavichar.model.Item
import com.example.kathavichar.model.Section
import com.example.kathavichar.model.SectionData

@Composable
fun HomeCategories(data: List<Section>?, navigationController: NavHostController) {
    Column {
        data?.forEach { sectionData ->
            Text(sectionData.sectionName, fontSize = 20.sp, modifier = Modifier.padding(8.dp))
            Spacer(modifier = Modifier.height(15.dp))
            Column {
                LazyRow(content = {
                    /*items(sectionData.data) {
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            PlayListItem(sectionData.data[it], navigationController)
                        }
                    }*/

                    items(getArtistDataSize(sectionData.data)) { index ->
                        val artist = when (sectionData.data) {
                            is ArtistData.ArtistsMap -> sectionData.data.artists.values.toList()[index]
                            is ArtistData.OthersList -> sectionData.data.others[index]
                        }
                        PlayListItem(ArtistSummary(artist.image, artist.name), navigationController)
                    }
                })
            }
            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}

fun getArtistDataSize(artistData: ArtistData): Int {
    return when (artistData) {
        is ArtistData.ArtistsMap -> artistData.artists.size
        is ArtistData.OthersList -> artistData.others.size
    }
}

@Composable
fun PlayListItem(sectionItem: ArtistSummary, navigationController: NavHostController) {
    Card(
        elevation = 4.dp,
        shape = RoundedCornerShape(10.dp),
        modifier = Modifier
            .size(150.dp, 150.dp)
            .padding(3.dp)
            .clickable { navigationController.navigate("${Screen.SongsList.route}/${sectionItem.name.toString()}") },
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(102.dp),
            modifier = Modifier.height(IntrinsicSize.Max),
        ) {
            Box() {
                AsyncImage(
                    model = sectionItem.image,
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
