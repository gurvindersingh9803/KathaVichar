package com.example.kathavichar.view.categories

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.kathavichar.R
import com.example.kathavichar.model.Artist
import com.example.kathavichar.model.Section
import com.example.kathavichar.viewModel.MainViewModel

@Composable
fun PlayList(data: List<Artist>?, navigationController: NavHostController) {
    Log.i("dfswgdfsg", data.toString())
    LazyVerticalStaggeredGrid(columns = StaggeredGridCells.Fixed(2), content = {
        data?.let {
            items(data.size) {
                // PlayListItem(data[it], navigationController)
            }
        }
    })
}

@Composable
fun PlayListItem(category: Section, navigationController: NavHostController) {
    Card(elevation = 4.dp, modifier = Modifier.padding(3.dp).clickable { }) {
        Column {
            /*Box(modifier = Modifier.fillMaxSize()) {
                AsyncImage(
                    model = category.imageUrl,
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .height(200.dp),
                )
            }*/
            Box(modifier = Modifier.fillMaxSize().background(color = Color(0Xffff9f64))) {
             //   Text(category.nickname.toString(), fontSize = 30.sp, color = Color.White.copy(alpha = 0.5f), modifier = Modifier.align(Alignment.BottomEnd))
            }
        }
    }
}

@Composable
fun isDataLoading() {
    Text(text = "Loading.....")
}

@Composable
fun Prevew() {
    Column {
        Text(text = "Artists")
        LazyRow(
            content = {
                items(10) {
                    Box(
                        Modifier
                            .size(150.dp),
                    ) {
                        Card(
                            modifier = Modifier.padding(10.dp).height(IntrinsicSize.Min),
                            elevation = 10.dp,
                            shape = RoundedCornerShape(4.dp),
                        ) {
                            Column {
                                Box(modifier = Modifier.fillMaxSize()) {
                                    Image(
                                        painter = painterResource(id = R.drawable.img),
                                        contentDescription = null,
                                    )
                                }
                                Box(
                                    modifier = Modifier.fillMaxSize()
                                        .background(color = Color(0Xffff9f64)),
                                ) {
                                    Text(
                                        "Maskeen Ji",
                                        fontSize = 30.sp,
                                        color = Color.Black.copy(alpha = 0.5f),
                                        modifier = Modifier.align(Alignment.BottomEnd),
                                    )
                                }
                            }
                        }
                    }
                }
            },
        )
    }
}

@Composable
fun hey(viewModel: MainViewModel) {
    LazyRow(
        content = {
            items(19) {
                Text("adnccc")
            }
        },
    )
}
