package com.example.kathavichar.view.categories

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
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
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImage
import com.example.kathavichar.model.Category

@Composable
fun PlayList(data: List<Category>?, navigationController: NavHostController) {
    LazyVerticalStaggeredGrid(columns = StaggeredGridCells.Fixed(2), content = {
        data?.let {
            items(data.size) {
                PlayListItem(data[it], navigationController)
            }
        }
    })
}

@Composable
fun PlayListItem(category: Category, navigationController: NavHostController) {
    Card(elevation = 4.dp, modifier = Modifier.padding(3.dp).clickable { navigationController.navigate("SongsList") }) {
        Column {
            Box(modifier = Modifier.fillMaxSize()) {
                AsyncImage(
                    model = category.imageUrl,
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .height(200.dp),
                )
            }
            Box(modifier = Modifier.fillMaxSize().background(color = Color(0Xffff9f64))) {
                Text(category.nickname.toString(), fontSize = 30.sp, color = Color.White.copy(alpha = 0.5f), modifier = Modifier.align(Alignment.BottomEnd))
            }
        }
    }
}

@Composable
fun isDataLoading() {
    Text(text = "Loading.....")
}
