package com.example.kathavichar.view
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Scaffold
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.example.kathavichar.common.BottomNavigationBar
import com.example.kathavichar.common.NavigationGraph
import com.example.kathavichar.common.utils.KathaVicharTopBar
import com.example.kathavichar.di.KathaVicharApp
import com.example.kathavichar.ui.theme.KathaVicharTheme
import com.example.kathavichar.viewModel.MainViewModel

class MainActivity : ComponentActivity() {
    private val mainViewModel by viewModels<MainViewModel>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            KathaVicharTheme {
                val navController = rememberNavController()
                Scaffold(
                    // topBar = { KathaVicharTopBar() },
                    bottomBar = {
                        BottomNavigationBar(navigationController = navController)
                    },
                ) { innerPadding ->
                    Box(modifier = Modifier.padding(innerPadding)) {
                        NavigationGraph(navigationController = navController, mainViewModel)
                    }
                }
            }
        }
    }
}
