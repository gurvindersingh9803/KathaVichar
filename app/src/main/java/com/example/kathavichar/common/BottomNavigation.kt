package com.example.kathavichar.common

import android.graphics.drawable.Icon
import androidx.compose.material.BottomNavigation
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState

/*@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CommonBaseBottomNavigationBar(items: List<BottomNavigationScreens>, navigationController: Nav) {
    Scaffold(
        bottomBar = {
            BottomNavigation {
                val currentRoute = currentRoute(navigationController)
                items.forEach {
                    NavigationBarItem(selected = currentRoute == "Main", icon = {  },
                        onClick = { if (currentRoute != "Main") { } })
                }
            }
        },
    ) {

    }
}*/

@Composable
private fun currentRoute(navController: NavHostController): String? {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    return navBackStackEntry?.arguments?.getString("Main")
}
