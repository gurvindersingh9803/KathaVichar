package com.example.kathavichar.common

import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.kathavichar.view.categories.PlayList

@Composable
fun BottomNavigationBar() {
    val navigationController = rememberNavController()
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
        NavigationGraph(navigationController)
    }
}

@Composable
fun NavigationGraph(navigationController: NavHostController) {
    NavHost(navController = navigationController, startDestination = Screen.MainPlayList.route) {
        composable(route = Screen.MainPlayList.route) {
            // navigationController.navigate(route = route)
            PlayList()
        }
        composable(route = Screen.Download.route) {
            DownloadScreen()
        }
    }
}

@Composable
fun DownloadScreen() {
    Text(text = "Download")
}

@Composable
fun MainActivityScreen() {
    Text(text = "Main")
}
