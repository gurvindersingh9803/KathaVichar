package com.example.kathavichar.common

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.Text
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.kathavichar.R
import com.example.kathavichar.view.home.categories.MainScreen
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
            MainScreen(navigationController, viewModel)
        }
        composable(route = Screen.Download.route) {
            DownloadScreen()
        }
        composable(route = Screen.SongsList.route) {
            SongsListScreen()
        }
    }
}

@Composable
fun DownloadScreen() {
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SongsListScreen() {
    // val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior(rememberTopAppBarState())

   /* Scaffold(
        topBar = {
            LargeTopAppBar(

                title = {
                    Text(text = "Maskeen Ji")
                },

                scrollBehavior = scrollBehavior,
            )
        },
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection).background(Color.Red),

    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
        ) {
            items(10) { item ->
                Text(
                    text = item.toString(),
                )
            }
        }
    }*/
    val lazyListState = rememberLazyListState()

    var scrolledY = 0f
    var previousOffset = 0

    LazyColumn(content = {
        item {
            Card(Modifier.padding(10.dp)) {
                Image(
                    painter = painterResource(id = R.drawable.imag),
                    contentDescription = null,
                    contentScale = ContentScale.FillWidth,
                    modifier = Modifier
                        .graphicsLayer {
                            scrolledY += lazyListState.firstVisibleItemScrollOffset - previousOffset
                            translationY = scrolledY * 0.5f
                            previousOffset = lazyListState.firstVisibleItemScrollOffset
                        }
                        .height(200.dp)
                        .fillMaxWidth()
                        .fillMaxHeight(),
                )
            }
        }
        items(30) {
            SongItem()
        }
    })
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview
@Composable
fun SongItem() {
    Column() {
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
                Text(text = "Dasam Granth", fontSize = 12.sp)
                Text(text = "Track name", fontSize = 18.sp, fontWeight = FontWeight.Bold)
            }
            Text(text = "2:35", fontSize = 12.sp)
        }

        Spacer(modifier = Modifier.height(20.dp))
    }
}
