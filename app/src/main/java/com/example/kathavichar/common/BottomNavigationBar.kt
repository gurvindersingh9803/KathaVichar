package com.example.kathavichar.common

import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.navArgument
import androidx.navigation.navDeepLink
import com.example.kathavichar.view.composables.home.HomeScreenState
import com.example.kathavichar.view.composables.songs.SongsListState
import com.example.kathavichar.viewModel.MainViewModel
import com.example.kathavichar.viewModel.SongsViewModel

@Composable
fun BottomNavigationBar(navigationController: NavHostController) {
    val screens = listOf(Screen.MainPlayList, Screen.Download)

    BottomNavigation(
        backgroundColor = MaterialTheme.colors.primary,
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
fun NavigationGraph(
    navigationController: NavHostController,
    mainViewModel: MainViewModel,
    songsViewModel: SongsViewModel,
) {
    NavHost(navController = navigationController, startDestination = Screen.MainPlayList.route) {
        composable(route = Screen.MainPlayList.route) {
            HomeScreenState(navigationController, mainViewModel)
        }
        composable(route = Screen.Download.route) {
            DownloadScreen()
        }
        composable(
            route = "${Screen.SongsList.route}/{artistName}",
            arguments =
            listOf(
                navArgument("artistName") {
                    type = NavType.StringType
                },
            ),
            deepLinks =
            listOf(
                navDeepLink { uriPattern = "musify://songslist/{artistName}" },
            ),
        ) {
            val artistName = it.arguments?.getString("artistName")
            SongsListState(artistName.toString(), songsViewModel)
        }
    }
}

@Composable
fun DownloadScreen() {
}
