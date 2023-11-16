package com.example.kathavichar.common

import android.util.Log
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.lifecycle.ViewModel
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.kathavichar.R
import com.example.kathavichar.view.MainScreen
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


