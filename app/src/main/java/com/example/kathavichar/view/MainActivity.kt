package com.example.kathavichar.view
import android.Manifest
import android.app.Activity
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.core.view.WindowCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.navigation.compose.rememberNavController
import com.example.kathavichar.common.NavigationGraph
import com.example.kathavichar.common.sharedComposables.ScaffoldWithTopBar
import com.example.kathavichar.repositories.musicPlayer.MediaService
import com.example.kathavichar.ui.theme.KathaVicharTheme
import com.example.kathavichar.viewModel.MainViewModel
import com.example.kathavichar.viewModel.SongsViewModel
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.gson.Gson
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel

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
                enableEdgeToEdge()
                val isPermissionGranted =
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        rememberPermissionState(Manifest.permission.POST_NOTIFICATIONS)
                    } else {
                        null
                    }

                val lifeCycleOwner = LocalLifecycleOwner.current
                val notificationPermissionState =
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        rememberPermissionState(Manifest.permission.POST_NOTIFICATIONS)
                    } else {
                        null
                    }

                DisposableEffect(key1 = lifeCycleOwner) {
                    val observer =
                        LifecycleEventObserver { _, event ->
                            if (event == Lifecycle.Event.ON_RESUME) {
                                if (notificationPermissionState != null) {
                                    if (!notificationPermissionState.status.isGranted) {
                                        Log.d("Permissions", "Requesting notification permission")
                                        notificationPermissionState.launchPermissionRequest()
                                    } else {
                                        Log.d("Permissions", "Notification permission already granted")
                                        startMusicService() // Start the service only if permission is granted
                                    }
                                } else {
                                    startMusicService() // Start the service if no permission is required
                                }
                            }
                        }
                    lifeCycleOwner.lifecycle.addObserver(observer)
                    onDispose {
                        lifeCycleOwner.lifecycle.removeObserver(observer)
                    }
                }

                val navController = rememberNavController()
                ScaffoldWithTopBar(
                    false,
                    title = "Katha Vaachaks",
                    actions = {
                    },
                    { innerPadding ->
                        Box(modifier = Modifier.padding()) {
                            // SongScreenParent(songsViewModel)

                            NavigationGraph(
                                innerPadding,
                                navigationController = navController,
                                mainViewModel,
                                songsViewModel,
                            )
                        }

                        MyEventListener {
                            when (it) {
                                Lifecycle.Event.ON_RESUME -> {
                                    println("ON_RESUME fgsdf ${songsViewModel.selectedTrack}")
                                }

                                Lifecycle.Event.ON_PAUSE -> {
                                    println("ON_PAUSE fgsdf ${songsViewModel.selectedTrack}")
                                }

                                else -> {}
                            }
                        }

                        /*Surface(modifier = Modifier.fillMaxSize()) {
                                        // Setup the HomeScreenParent with the viewModel.
                                        SongScreenParent(songsViewModel)
                                    }*/
                    },
                ) {
                }
            }
        }
    }

    private fun startMusicService() {
        try {
            if (!isServiceRunning) {
                val intent = Intent(this, MediaService::class.java)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    startForegroundService(intent)
                } else {
                    startService(intent)
                }
                isServiceRunning = true
            }
        } catch (e: Exception) {
            println("fgbhdfghd $e")
        }
    }

    override fun onResume() {
        super.onResume()
    }

    override fun onDestroy() {
        super.onDestroy()
        songsViewModel.selectedTrack?.let { songsViewModel.savePlaybackState(it) }
    }
}

@Composable
fun MyEventListener(OnEvent: (event: Lifecycle.Event) -> Unit) {
    val eventHandler = rememberUpdatedState(newValue = OnEvent)
    val lifecycleOwner = rememberUpdatedState(newValue = LocalLifecycleOwner.current)

    DisposableEffect(lifecycleOwner.value) {
        val lifecycle = lifecycleOwner.value.lifecycle
        val observer =
            LifecycleEventObserver { source, event ->
                eventHandler.value(event)
            }

        lifecycle.addObserver(observer)

        onDispose {
            lifecycle.removeObserver(observer)
        }
    }
}

fun Activity.setStatusBarColor(color: Color, darkIcons: Boolean? = null) {
    Log.d("StatusBar", "Color: ${Color(0xFF6200EE).luminance()}, DarkIcons: false")
    window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
    window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
    window.statusBarColor = color.toArgb()

    val autoDarkIcons = darkIcons ?: (color.luminance() > 0.5f) // Adjust based on brightness
    WindowCompat.getInsetsController(window, window.decorView)?.isAppearanceLightStatusBars = false
}