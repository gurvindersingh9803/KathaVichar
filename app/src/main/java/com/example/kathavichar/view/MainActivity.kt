package com.example.kathavichar.view

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.StringRes
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material.BottomNavigation
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.kathavichar.R
import com.example.kathavichar.common.DownloadScreen
import com.example.kathavichar.common.MainActivityScreen
import com.example.kathavichar.repositories.FirebaseTestRepo
import com.example.kathavichar.ui.theme.KathaVicharTheme
import com.example.kathavichar.viewModel.MainViewModel
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    private lateinit var firebaseTestRepo: FirebaseTestRepo
    private lateinit var mainViewModel: MainViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mainViewModel = MainViewModel()

        lifecycleScope.launch {
            mainViewModel.GetCategories()
        }
        setContent {
            KathaVicharTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background,
                ) {
                    CommonBaseBottomNavigationBar()
                    // RenderGridView(mainViewModel)
                }
            }
        }
    }
}

@Composable
fun RenderGridView(mainViewModel: MainViewModel) {
    val categories by mainViewModel._categories.observeAsState()
    Log.i("lkjhjkhkj", categories.toString())
    LazyVerticalGrid(columns = GridCells.Fixed(2), content = {
        categories?.size?.let {
            items(it) {
                Text(text = categories!![it].nickname.toString())
            }
        }
    })
}

@Composable
fun Greeting(name: String) {
    Text(text = "Hello $name!")
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    KathaVicharTheme {
        Greeting("Android")
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CommonBaseBottomNavigationBar() {
    val navigationController = rememberNavController()
    val screens = listOf(Screen.Main, Screen.Download)
    Scaffold(
        bottomBar = {
            val navBackStackEntry by navigationController.currentBackStackEntryAsState()
            val currentDestination = navBackStackEntry?.destination
            BottomNavigation {
                screens.forEach {screen->
                    NavigationBarItem(
                        selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true,
                        icon = { },
                        onClick = {
                            navigationController.navigate(screen.route) {
                                // Pop up to the start destination of the graph to
                                // avoid building up a large stack of destinations
                                // on the back stack as users select items
                                popUpTo(navigationController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                // Avoid multiple copies of the same destination when
                                // reselecting the same item
                                launchSingleTop = true
                                // Restore state when reselecting a previously selected item
                                restoreState = true
                            }
                        },
                    )
                }
            }
        },
    ) { innerPadding ->
        NavigationGraph()
    }
}

@Composable
fun NavigationGraph() {
    val navigationController = rememberNavController()
    NavHost(navController = navigationController, startDestination = "Download") {
        composable(route = Screen.Main.route) {
            // navigationController.navigate(route = route)
            MainActivityScreen()
        }
        composable(route = Screen.Download.route) {
            DownloadScreen()
        }
    }
}

sealed class Screen(val route: String) {
    object Main : Screen("Main")
    object Download : Screen("Download")
}
