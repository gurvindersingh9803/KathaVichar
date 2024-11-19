package com.example.kathavichar.view
import android.Manifest
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material3.Surface
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.navigation.compose.rememberNavController
import com.example.kathavichar.common.NavigationGraph
import com.example.kathavichar.repositories.musicPlayer.MediaService
import com.example.kathavichar.ui.theme.KathaVicharTheme
import com.example.kathavichar.view.composables.songs.typography
import com.example.kathavichar.viewModel.MainViewModel
import com.example.kathavichar.viewModel.SongsViewModel
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState

class MainActivity : ComponentActivity() {
    private val mainViewModel by viewModels<MainViewModel>()
    private val songsViewModel by viewModels<SongsViewModel>()
    private var isServiceRunning = false

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    @OptIn(ExperimentalPermissionsApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // TODO: stop firebase duplicacy of data.
        setContent {
            KathaVicharTheme {
                val isPermissionGranted =
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        rememberPermissionState(Manifest.permission.POST_NOTIFICATIONS)
                    } else {
                        null
                    }

                val lifeCycleOwner = LocalLifecycleOwner.current

                DisposableEffect(key1 = lifeCycleOwner) {
                    val observer =
                        LifecycleEventObserver { _, event ->
                            if (event == Lifecycle.Event.ON_RESUME) {
                                if (isPermissionGranted != null) {
                                    if (!isPermissionGranted.status.isGranted) {
                                        Log.d("Permissions", "Requesting permission")
                                        isPermissionGranted.launchPermissionRequest()
                                    } else {
                                        Log.d("Permissions", "Permission already granted")
                                    }
                                }
                            }
                        }
                    lifeCycleOwner.lifecycle.addObserver(observer)
                    onDispose {
                        lifeCycleOwner.lifecycle.removeObserver(observer)
                    }
                }

                startMusicService()

                val navController = rememberNavController()
                Scaffold(topBar = {
                    Surface(shadowElevation = 5.dp) {
                        TopAppBar(
                            title = {
                                Text(
                                    text = "Katha Veechar",
                                    style = typography.titleLarge,
                                )
                            },
                        )
                    }
                }) { innerPadding ->
                    Box(modifier = Modifier.padding(innerPadding)) {
                        // SongScreenParent(songsViewModel)

                        NavigationGraph(navigationController = navController, mainViewModel, songsViewModel)
                    }
                }

                /*Surface(modifier = Modifier.fillMaxSize()) {
                    // Setup the HomeScreenParent with the viewModel.
                    SongScreenParent(songsViewModel)
                }*/
            }
        }
    }

    private fun startMusicService() {
        if (!isServiceRunning) {
            val intent = Intent(this, MediaService::class.java)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                startForegroundService(intent)
            } else {
                startService(intent)
            }
            isServiceRunning = true
        }
    }
}
