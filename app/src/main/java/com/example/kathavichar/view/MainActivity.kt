package com.example.kathavichar.view
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.material.Text
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.kathavichar.common.NavigationGraph
import com.example.kathavichar.network.ServerResponse
import com.example.kathavichar.ui.theme.KathaVicharTheme
import com.example.kathavichar.view.categories.PlayList
import com.example.kathavichar.view.categories.isDataLoading
import com.example.kathavichar.viewModel.MainViewModel

class MainActivity : ComponentActivity() {
    private val mainViewModel by viewModels<MainViewModel> ()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            KathaVicharTheme {
                val navController = rememberNavController()
                NavigationGraph(navigationController = navController, mainViewModel)
            }
        }
    }
}

@Composable
fun Greeting(name: String) {
    Text(text = "Hello $name!")
}

@Composable
fun MainScreen(navigationController: NavHostController, viewModel: MainViewModel) {
    Scaffold() {
/
    }
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
