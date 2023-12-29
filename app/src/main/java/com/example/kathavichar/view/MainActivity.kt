package com.example.kathavichar.view
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Scaffold
import androidx.compose.ui.Modifier
import androidx.media3.exoplayer.ExoPlayer
import androidx.navigation.compose.rememberNavController
import com.example.kathavichar.common.BottomNavigationBar
import com.example.kathavichar.common.NavigationGraph
import com.example.kathavichar.ui.theme.KathaVicharTheme
import com.example.kathavichar.view.musicPlayerService.MusicPlayerService
import com.example.kathavichar.viewModel.MainViewModel
import com.example.kathavichar.viewModel.MusicPlayerViewModel
import com.example.kathavichar.viewModel.SongsViewModel
import org.koin.android.ext.android.inject

class MainActivity : ComponentActivity() {
    private val mainViewModel by viewModels<MainViewModel>()
    private val songsViewModel by viewModels<SongsViewModel>()
    private val musicPlayerViewModel by viewModels<MusicPlayerViewModel>()
    private val musicPlayerService: MusicPlayerService by inject()

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
                        NavigationGraph(navigationController = navController, mainViewModel, songsViewModel, musicPlayerViewModel, musicPlayerService)
                    }
                }
            }
        }
    }
}
