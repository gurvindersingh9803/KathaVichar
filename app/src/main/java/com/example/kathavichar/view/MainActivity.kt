package com.example.kathavichar.view
import android.Manifest
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.AlertDialog
import androidx.compose.material.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.RememberObserver
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.media3.common.Player
import androidx.media3.session.MediaController
import androidx.media3.session.SessionToken
import androidx.navigation.compose.rememberNavController
import com.example.kathavichar.common.AndroidNetworkStatusProvider
import com.example.kathavichar.common.NavigationGraph
import com.example.kathavichar.common.SharedPrefsManager
import com.example.kathavichar.common.sharedComposables.ScaffoldWithTopBar
import com.example.kathavichar.repositories.musicPla.MusicPlayerKathaVichar
import com.example.kathavichar.repositories.musicPlayer.MediaService
import com.example.kathavichar.ui.theme.KathaVicharTheme
import com.example.kathavichar.viewModel.MainViewModel
import com.example.kathavichar.viewModel.SongsViewModel
import com.example.kathavichar.viewModel.SplashScreenViewModel
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionStatus
import com.google.accompanist.permissions.rememberPermissionState
import com.google.common.util.concurrent.ListenableFuture
import com.google.common.util.concurrent.MoreExecutors
import org.koin.java.KoinJavaComponent.inject

class MainActivity : ComponentActivity() {
    private val mainViewModel by viewModels<MainViewModel>()
    private val songsViewModel by viewModels<SongsViewModel>()
    private val splashScreenViewModel by viewModels<SplashScreenViewModel>()
    private val musicPlayerKathaVichar: MusicPlayerKathaVichar by inject(MusicPlayerKathaVichar::class.java)
    private val androidNetworkStatusProvider: AndroidNetworkStatusProvider by inject(AndroidNetworkStatusProvider::class.java)
    private var isServiceRunning = false
    private val sharedPreferences: SharedPrefsManager by inject(SharedPrefsManager::class.java)

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    @OptIn(ExperimentalPermissionsApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // TODO: stop firebase duplicacy of data.

        val appVersion = getAppVersion()

        // Restore playback state only if app was previously terminated

        if (
            !songsViewModel.isPlaybackRestored &&
            musicPlayerKathaVichar.mediaController?.currentMediaItem != null &&
            (
                musicPlayerKathaVichar.mediaController?.playbackState == Player.STATE_READY || // Song is ready (either playing or paused)
                    musicPlayerKathaVichar.mediaController?.playbackState == Player.STATE_BUFFERING // Song is buffering (might play soon)
                )
        ) {
            songsViewModel.isPlaybackRestored = true

            // Handle restoration of playback
            if (musicPlayerKathaVichar.mediaController?.playWhenReady == true) {
                // Song is actively playing
                // Proceed with restoring or resuming playback
                songsViewModel.restorePlaybackState()
                songsViewModel.isPlaybackRestored = true // Prevents multiple restores
            } else {
                // Song is paused
                // Handle restoration logic for paused state (e.g., resume from the current position)
                songsViewModel.restorePlaybackState()
                songsViewModel.isPlaybackRestored = true // Prevents multiple restores
            }
            println("sfgsdghsfl;;[ ${songsViewModel.isPlaybackRestored}")
        }

        musicPlayerKathaVichar.initializeMediaController()
        setContent {
            KathaVicharTheme {
                enableEdgeToEdge()
                musicPlayerKathaVichar.initializeMediaController()

                // Handle permission logic

                val navController = rememberNavController()
                ScaffoldWithTopBar(
                    false,
                    title = "Katha Vaachaks",
                    actions = {
                    },
                    { innerPadding ->

                        Box(modifier = Modifier.padding()) {
                            // SongScreenParent(songsViewModel)
                            //HandleNotificationPermission()

                            NavigationGraph(
                                innerPadding,
                                navigationController = navController,
                                mainViewModel,
                                songsViewModel,
                                splashScreenViewModel,
                                appVersion,
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
                    },
                ) {
                }
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    @OptIn(ExperimentalPermissionsApi::class)
    @Composable
    fun HandleNotificationPermission() {
        val notificationPermissionState = rememberPermissionState(permission = Manifest.permission.POST_NOTIFICATIONS)
        val context = LocalContext.current

        // Track if the permission request has been processed after user interaction
        var hasRequestedPermission by rememberSaveable { mutableStateOf(false) }
        var permissionRequestCompleted by rememberSaveable { mutableStateOf(false) }

        LaunchedEffect(notificationPermissionState.status) {

            // Check if the permission state has changed after the request
            if (hasRequestedPermission) {
                println("ertyheuhrwtdwty ${notificationPermissionState.status}")
                permissionRequestCompleted = true
            }
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
        ) {
            when (val status = notificationPermissionState.status) {
                is PermissionStatus.Granted -> {
                    // Permission granted, show success message
                    Text("Camera permission granted. You can now use the camera.")
                    Button(onClick = { }, Modifier.padding(top = 16.dp)) {
                        Text("Go Back")
                    }
                }
                is PermissionStatus.Denied -> {
                    if (permissionRequestCompleted) {
                        // Show rationale only after the permission request is completed
                        if (status.shouldShowRationale) {
                            Text("Camera permission is required to use this feature.")
                            Button(onClick = {
                                notificationPermissionState.launchPermissionRequest()
                                hasRequestedPermission = true
                            }) {
                                Text("Request Camera Permission")
                            }
                        } else {
                            // Show "Denied" message only after the user has denied permission
                            Text("Camera permission denied. Please enable it in the app settings to proceed.")
                            Button(onClick = {
                                // Open app settings to manually enable the permission
                                val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                                    data = Uri.fromParts("package", context.packageName, null)
                                }
                                context.startActivity(intent)
                            }) {
                                Text("Open App Settings")
                            }
                        }
                    } else {
                        // Show the initial request button
                        Button(onClick = {
                            notificationPermissionState.launchPermissionRequest()
                            hasRequestedPermission = true
                        }) {
                            Text("Request Camera Permission")
                        }
                    }
                }
            }
        }
    }

    @Composable
    fun PermissionRationaleDialog(onConfirm: () -> Unit, onDismiss: () -> Unit) {
        AlertDialog(
            onDismissRequest = onDismiss,
            title = { Text("Permission Required") },
            text = { Text("This app needs notification permission to show playback controls. Please grant it.") },
            confirmButton = {
                TextButton(onClick = onConfirm) {
                    Text("Grant")
                }
            },
            dismissButton = {
                TextButton(onClick = onDismiss) {
                    Text("Deny")
                }
            },
        )
    }

    @Composable
    fun PermissionDeniedDialog(onDismiss: () -> Unit) {
        val context = LocalContext.current
        AlertDialog(
            onDismissRequest = onDismiss,
            title = { Text("Permission Denied") },
            text = { Text("Notification permission was denied. Please enable it in app settings.") },
            confirmButton = {
                TextButton(onClick = {
                    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                        data = "package:${context.packageName}".toUri()
                    }
                    context.startActivity(intent)
                    onDismiss()
                }) {
                    Text("Go to Settings")
                }
            },
        )
    }
    private fun startMusicService() {
        try {
            println("khgkjhkghgjh $isServiceRunning")
            if (!isServiceRunning) {
                val intent = Intent(this, MediaService::class.java)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    println("khgkjhkghgjh 1 $isServiceRunning")
                    startForegroundService(intent)
                } else {
                    println("khgkjhkghgjh 2 $isServiceRunning")

                    startService(intent)
                }
                isServiceRunning = true
            }
        } catch (e: Exception) {
            println("fgbhdfghd $e")
        }
    }

    override fun onDestroy() {
        super.onDestroy()
    }

    // Function to fetch the app version using PackageManager
    private fun getAppVersion(): String {
        return try {
            val packageInfo: PackageInfo = packageManager.getPackageInfo(packageName, 0)
            packageInfo.versionName ?: "Version not found" // Handles null versionName gracefully
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
            "Version not found"
        }
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

/**
 * A Composable function that provides a managed MediaController instance.
 *
 * @param lifecycle The lifecycle of the owner of this MediaController. Defaults to the lifecycle of the LocalLifecycleOwner.
 * @return A State object containing the MediaController instance. The Composable will automatically re-compose whenever the state changes.
 */
@Composable
fun rememberManagedMediaController(
    lifecycle: Lifecycle = LocalLifecycleOwner.current.lifecycle,
): State<MediaController?> {
    // Application context is used to prevent memory leaks
    val appContext = LocalContext.current.applicationContext
    val controllerManager = remember { MediaControllerManager.getInstance(appContext) }

    // Observe the lifecycle to initialize and release the MediaController at the appropriate times.
    DisposableEffect(lifecycle) {
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_START -> controllerManager.initialize()
                Lifecycle.Event.ON_STOP -> controllerManager.release()
                else -> {}
            }
        }
        lifecycle.addObserver(observer)
        onDispose { lifecycle.removeObserver(observer) }
    }

    return controllerManager.controller
}

/**
 * A Singleton class that manages a MediaController instance.
 *
 * This class observes the Remember lifecycle to release the MediaController when it's no longer needed.
 */
internal class MediaControllerManager private constructor(context: Context) : RememberObserver {
    private val appContext = context.applicationContext
    private var factory: ListenableFuture<MediaController>? = null
    var controller = mutableStateOf<MediaController?>(null)
        private set

    init { initialize() }

    /**
     * Initializes the MediaController.
     *
     * If the MediaController has not been built or has been released, this method will build a new one.
     */
    @androidx.annotation.OptIn(androidx.media3.common.util.UnstableApi::class)
    internal fun initialize() {
        if (factory == null || factory?.isDone == true) {
            factory = MediaController.Builder(
                appContext,
                SessionToken(appContext, ComponentName(appContext, MediaService::class.java)),
            ).buildAsync()
        }
        factory?.addListener(
            {
                // MediaController is available here with controllerFuture.get()
                controller.value = factory?.let {
                    if (it.isDone) {
                        it.get()
                    } else {
                        null
                    }
                }
            },
            MoreExecutors.directExecutor(),
        )
    }

    /**
     * Releases the MediaController.
     *
     * This method will release the MediaController and set the controller state to null.
     */
    internal fun release() {
        factory?.let {
            MediaController.releaseFuture(it)
            controller.value = null
        }
        factory = null
    }

    // Lifecycle methods for the RememberObserver interface.
    override fun onAbandoned() { release() }
    override fun onForgotten() { release() }
    override fun onRemembered() {}

    companion object {
        @Volatile
        private var instance: MediaControllerManager? = null

        /**
         * Returns the Singleton instance of the MediaControllerManager.
         *
         * @param context The context to use when creating the MediaControllerManager.
         * @return The Singleton instance of the MediaControllerManager.
         */
        fun getInstance(context: Context): MediaControllerManager {
            return instance ?: synchronized(this) {
                instance ?: MediaControllerManager(context).also { instance = it }
            }
        }
    }
}
