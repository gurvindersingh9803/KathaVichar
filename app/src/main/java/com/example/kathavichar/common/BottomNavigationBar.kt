package com.example.kathavichar.common

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.Card
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import coil.compose.AsyncImage
import com.example.kathavichar.R
import com.example.kathavichar.model.Item
import com.example.kathavichar.model.SectionData
import com.example.kathavichar.network.ServerResponse
import com.example.kathavichar.viewModel.MainViewModel

/*@Composable
fun BottomNavigationBar(navigationController: NavHostController) {
    val screens = listOf(Screen.MainPlayList, Screen.Download)
    Scaffold(
        bottomBar = {
            val navBackStackEntry by navigationController.currentBackStackEntryAsState()
            val currentDestination = navBackStackEntry?.destination
            BottomNavigation {
                screens.forEach { screen ->
                    BottomNavigationItem(
                        icon = { },
                        label = { Text(screen.route) },
                        selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true,
                        onClick = {
                            navigationController.navigate(screen.route) {
                                navigationController.graph.startDestinationRoute?.let { screen_route ->
                                    popUpTo(screen_route) {
                                        saveState = true
                                    }
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                    )
                }
            }
        },
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding)) {
            NavigationGraph(navigationController = navigationController)
        }
    }
}*/

@Composable
fun BottomNavigationBar(navigationController: NavHostController) {
    val screens = listOf(Screen.MainPlayList, Screen.Download)

    BottomNavigation(
        backgroundColor = colorResource(id = R.color.teal_200),
        contentColor = Color.Black,
    ) {
        val navBackStackEntry by navigationController.currentBackStackEntryAsState()
        val currentDestination = navBackStackEntry?.destination
        screens.forEach { screen ->
            BottomNavigationItem(
                icon = { },
                label = { Text(screen.route) },
                selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true,
                onClick = {
                    navigationController.navigate(screen.route) {
                        navigationController.graph.startDestinationRoute?.let { screen_route ->
                            popUpTo(screen_route) {
                                saveState = true
                            }
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
            )
        }
    }
}

@Composable
fun NavigationGraph(navigationController: NavHostController, viewModel: MainViewModel) {
    NavHost(navController = navigationController, startDestination = Screen.MainPlayList.route) {
        composable(route = Screen.MainPlayList.route) {
            // navigationController.navigate(route = route)
            Log.i("RenderGridViewjhgjh", "")
            //  Prevew()

            // hey(viewModel)
            MainScreen(navigationController, viewModel)
        }
        composable(route = Screen.Download.route) {
            Log.i("DownloadScreenjhgjh", "")
            DownloadScreen()
        }
        composable(route = Screen.SongsList.route) {
            Log.i("SongsListScreenjhgjh", "")

            SongsListScreen()
        }
    }
}

@Composable
fun DownloadScreen() {
    Text(text = "Download")
}

@Composable
fun SongsListScreen() {
    Text(text = "SongsList")
}

@Composable
fun MainScreen(navigationController: NavHostController, viewModel: MainViewModel) {
    LaunchedEffect(Unit) {
        Log.i("dghfrh", "")
        viewModel.getCategories()
    }
    val uiState by viewModel.uiState.collectAsState()

    when (uiState) {
        is ServerResponse.isLoading -> isDataLoading()
        is ServerResponse.onSuccess -> PlayList(uiState.data, navigationController)
        is ServerResponse.onError -> {}
    }
}

@Composable
fun isDataLoading() {
    Text(text = "Loading.....")
}

@Composable
fun PlayList(data: List<SectionData>?, navigationController: NavHostController) {
    Log.i("dfswgdfsg", data.toString())
    Column {
        data?.forEach { sectionData ->
            Text(sectionData.sectionName, fontSize = 20.sp)
            Column {
                LazyRow(content = {
                    items(sectionData.data.size) {
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            PlayListItem(sectionData.data[it], navigationController)
                        }
                    }
                })
            }
        }
    }
        /*LazyColumn(content = {
        data?.forEach { sectionData ->
            item {
                Text(text = sectionData.sectionName)
            }

            items(sectionData.data.size) {
                Text(text = sectionData.data[it].name)
            }
        }
    })*/
}

@Composable
fun PlayListItem(sectionItem: Item, navigationController: NavHostController) {
    Card(
        elevation = 4.dp,
        modifier = Modifier
            .padding(3.dp)
            .clickable { },
    ) {
        Column {
            Box() {
                AsyncImage(
                    model = sectionItem.image,
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .height(150.dp),
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
