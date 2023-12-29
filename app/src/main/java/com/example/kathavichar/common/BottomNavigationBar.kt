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
import com.example.kathavichar.model.Song
import com.example.kathavichar.view.composables.home.HomeScreenState
import com.example.kathavichar.view.composables.musicPlayer.MusicPlayerState
import com.example.kathavichar.view.composables.songs.SongsListState
import com.example.kathavichar.view.musicPlayerService.MusicPlayerService
import com.example.kathavichar.viewModel.MainViewModel
import com.example.kathavichar.viewModel.MusicPlayerViewModel
import com.example.kathavichar.viewModel.SongsViewModel
import com.google.gson.Gson

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
    musicPlayerViewModel: MusicPlayerViewModel,
    musicPlayerService: MusicPlayerService,
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
            arguments = listOf(
                navArgument("artistName") {
                    type = NavType.StringType
                },
            ),
        ) {
            val artistName = it.arguments?.getString("artistName")
            SongsListState(navigationController, artistName, songsViewModel)
        }
        composable(
            route = "${Screen.MusicPlayerState.route}/{songItemString}",
            arguments = listOf(
                navArgument("songItemString") {
                    type = NavType.StringType
                },
            ),
        ) {
            it.arguments?.getString("songItemString").let { songItemString ->
                val gson = Gson()
                val audioUrl = gson.fromJson(songItemString, Song::class.java)
                MusicPlayerState(musicPlayerViewModel, musicPlayerService, audioUrl.audioUrl)
            }
        }
    }
}

@Composable
fun DownloadScreen() {
}

/*
@Previe
@Composable
fun MusicPlayer() {
    val gradientColors =
        listOf(
            MaterialTheme.colors.primary,
            MaterialTheme.colors.background,
        )

    val sliderColors = if (isSystemInDarkTheme()) {
        SliderDefaults.colors(
            thumbColor = MaterialTheme.colors.onBackground,
            activeTrackColor = MaterialTheme.colors.onBackground,
            inactiveTrackColor = MaterialTheme.colors.onBackground.copy(
                alpha = ProgressIndicatorDefaults.IndicatorBackgroundOpacity,
            ),
        )
    } else {
        SliderDefaults.colors(
            thumbColor = MaterialTheme.colors.background,
            activeTrackColor = MaterialTheme.colors.background,
            inactiveTrackColor = MaterialTheme.colors.background.copy(
                alpha = ProgressIndicatorDefaults.IndicatorBackgroundOpacity,
            ),
        )
    }

    Box(
        modifier = Modifier
            .fillMaxSize(),
    ) {
        Surface {
            Box(
                modifier = Modifier
                    .background(
                        Brush.verticalGradient(
                            colors = gradientColors,
                            endY = LocalConfiguration.current.screenHeightDp.toFloat() * LocalDensity.current.density,
                        ),
                    )
                    .fillMaxSize()
                    .systemBarsPadding(),
            ) {
                Column {
                    IconButton(
                        onClick = {},
                    ) {
                        Image(
                            imageVector = Icons.Rounded.KeyboardArrowDown,
                            contentDescription = "Close",
                            colorFilter = ColorFilter.tint(LocalContentColor.current),
                        )
                    }
                    Column(
                        modifier  Modifier
                            .padding(horizontal = 24.dp),
                    ) {
                        Box(
                            modifier = Modifier
                                .padding(vertical = 32.dp)
                                .clip(MaterialTheme.shapes.medium)
                                .weight(1f, fill = false)
                                .aspectRatio(1f),

                        ) {
                          */
/*  Image(
                                painter = painterResource(id = R.drawable.imag),
                                contentDescription = "Song thumbnail",
                                contentScale = ContentScale.Crop,
                                modifier = Modifier
                                    .fillMaxSize(),
                            )*//*

                            // VinylAnimation(painter = painterResource(id = R.drawable.imag), isSongPlaying = true)
                        }

                        Text(
                            text = "Sant maskeen ji",
                            style = MaterialTheme.typography.h5,
                            color = MaterialTheme.colors.onBackground,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                        )

                        Text(
                            "Sub title",
                            style = MaterialTheme.typography.subtitle1,
                            color = MaterialTheme.colors.onBackground,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.graphicsLayer {
                                alpha = 0.60f
                            },
                        )

                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 24.dp),
                        ) {
                            Slider(
                                value = 50F,
                                modifier = Modifier
                                    .fillMaxWidth(),
                                colors = sliderColors,
                                onValueChange = {},
                                onValueChangeFinished = {},
                            )
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                CompositionLocalProvider(LocalContentAlpha provides ContentAlpha.medium) {
                                    Text(
                                        "DateTime",
                                        style = MaterialTheme.typography.body2,
                                    )
                                }
                                CompositionLocalProvider(LocalContentAlpha provides ContentAlpha.medium) {
                                    Text(
                                        "Total DateTime",
                                        style = MaterialTheme.typography.body2,
                                    )
                                }
                            }
                        }

                        Row(
                            horizontalArrangement = Arrangement.SpaceAround,
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp),
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.Add,
                                contentDescription = "Skip Previous",
                                modifier = Modifier
                                    .clip(CircleShape)
                                    .clickable(onClick = {})
                                    .padding(12.dp)
                                    .size(32.dp),
                            )
                            Icon(
                                imageVector = Icons.Rounded.Star,
                                contentDescription = "Replay 10 seconds",
                                modifier = Modifier
                                    .clip(CircleShape)
                                    .clickable(onClick = {})
                                    .padding(12.dp)
                                    .size(32.dp),
                            )
                            Icon(
                                painter = painterResource(R.drawable.imag),
                                contentDescription = "Play",
                                tint = MaterialTheme.colors.background,
                                modifier = Modifier
                                    .clip(CircleShape)
                                    .background(MaterialTheme.colors.onBackground)
                                    .clickable(onClick = {})
                                    .size(64.dp)
                                    .padding(8.dp),
                            )
                            Icon(
                                imageVector = Icons.Rounded.Close,
                                contentDescription = "Forward 10 seconds",
                                modifier = Modifier
                                    .clip(CircleShape)
                                    .clickable(onClick = {})
                                    .padding(12.dp)
                                    .size(32.dp),
                            )
                            Icon(
                                imageVector = Icons.Rounded.Star,
                                contentDescription = "Skip Next",
                                modifier = Modifier
                                    .clip(CircleShape)
                                    .clickable(onClick = {})
                                    .padding(12.dp)
                                    .size(32.dp),
                            )
                        }
                    }
                }
            }
        }
    }
}

*/
/*@Composable
fun VinylAnimation(
    modifier: Modifier = Modifier,
    isSongPlaying: Boolean = true,
    painter: Painter,
) {
    var currentRotation by remember {
        mutableFloatStateOf(0f)
    }

    val rotation = remember {
        Animatable(currentRotation)
    }

    LaunchedEffect(isSongPlaying) {
        if (isSongPlaying) {
            rotation.animateTo(
                targetValue = currentRotation + 360f,
                animationSpec = infiniteRepeatable(
                    animation = tween(3000, easing = LinearEasing),
                    repeatMode = RepeatMode.Restart,
                ),
            ) {
                currentRotation = value
            }
        } else {
            if (currentRotation > 0f) {
                rotation.animateTo(
                    targetValue = currentRotation + 50,
                    animationSpec = tween(
                        1250,
                        easing = LinearOutSlowInEasing,
                    ),
                ) {
                    currentRotation = value
                }
            }
        }
    }

    Vinyl(painter = painter, rotationDegrees = rotation.value)
}

@Composable
fun Vinyl(
    modifier: Modifier = Modifier,
    rotationDegrees: Float = 0f,
    painter: Painter,
) {
    Box(
        modifier = modifier
            .aspectRatio(1.0f)
            .clip(RoundedCornerShape(4.dp)),
    ) {
        // Vinyl background
        Image(
            modifier = Modifier
                .fillMaxSize()
                .rotate(rotationDegrees),
            painter = painterResource(id = R.drawable.imag),
            contentDescription = "Vinyl Background",
        )

        // Vinyl song cover
        Image(
            modifier = Modifier
                .fillMaxSize(0.5f)
                .rotate(rotationDegrees)
                .aspectRatio(1.0f)
                .align(Alignment.Center)
                .clip(RoundedCornerShape(6.dp)),
            painter = painter,
            contentDescription = "Song cover",
        )
    }
}*/
