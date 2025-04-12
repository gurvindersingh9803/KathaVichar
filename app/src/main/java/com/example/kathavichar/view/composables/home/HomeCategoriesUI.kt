package com.example.kathavichar.view.composables.home

import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.ModalBottomSheetLayout
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.rememberModalBottomSheetState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.airbnb.lottie.LottieProperty
import com.airbnb.lottie.compose.LottieDynamicProperty
import com.airbnb.lottie.compose.rememberLottieDynamicProperties
import com.airbnb.lottie.model.KeyPath
import com.example.kathavichar.common.Screen
import com.example.kathavichar.common.utils.isDataLoading
import com.example.kathavichar.model.ArtistsItem
import com.example.kathavichar.model.Songs
import com.example.kathavichar.network.ServerResponse
import com.example.kathavichar.repositories.musicPlayer.MusicPlayerStates
import com.example.kathavichar.view.composables.musicPlayer.BottomPlayerTab
import com.example.kathavichar.view.composables.musicPlayer.BottomSheetDialog
import com.example.kathavichar.view.composables.songs.LottieAnimationForPlayingSong
import com.example.kathavichar.view.composables.songs.md_theme_light_primary
import com.example.kathavichar.viewModel.MainViewModel
import com.example.kathavichar.viewModel.SongsViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterialApi::class)
@Composable
fun ArtistSearchScreen(
    artists: List<ArtistsItem>? = null,
    navigationController: NavHostController,
    innerPadding: PaddingValues,
    viewModel: MainViewModel,
    songsViewModel: SongsViewModel,
    errorMessage: String?,
) {
    var isExecuted by remember { mutableStateOf(false) }
    val filteredArtists by viewModel.filteredArtists.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val isLoading = viewModel.uiState.collectAsState().value is ServerResponse.isLoading
    val fullScreenState =
        rememberModalBottomSheetState(
            initialValue = ModalBottomSheetValue.Hidden,
            skipHalfExpanded = true,
        )
    val selectedTrack by rememberUpdatedState(songsViewModel.selectedTrack)
    val getShowBottomSheetOnRestartState by rememberUpdatedState(songsViewModel.getShowBottomSheetOnRestartState())
    val scope = rememberCoroutineScope()

    val onBottomTabClick: () -> Unit = {
        Log.d("BottomTab", "BottomTab clicked")
        scope.launch {
            if (fullScreenState.isVisible) {
                fullScreenState.hide()
            } else {
                fullScreenState.show()
            }
        }
    }

    // Check if track is playing when screen launches
    LaunchedEffect(getShowBottomSheetOnRestartState && selectedTrack != null) {
        scope.launch {
            if (getShowBottomSheetOnRestartState) {
                fullScreenState.show()
                songsViewModel.saveBottomSheetOnRestartState(false)
            }
        }
    }
    ModalBottomSheetLayout(
        sheetContent = {
            selectedTrack?.let { track ->
                BottomSheetDialog(track, songsViewModel, songsViewModel.playbackState, fullScreenState.isVisible)
            }
        },
        sheetState = fullScreenState,
        sheetShape = RoundedCornerShape(topStart = 10.dp, topEnd = 10.dp),
        sheetElevation = 12.dp,
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Column(modifier = Modifier.padding(horizontal = 20.dp)) {
                            Text(
                                "Explore Your Spiritual Journey",
                                style = MaterialTheme.typography.displayLarge.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 20.sp,
                                    color = Color(0xFF909090),
                                ),
                            )
                            androidx.compose.material.Divider(
                                modifier = Modifier
                                    .fillMaxWidth() // Make sure it spans the full width
                                    .height(1.dp), // Increased height to make it visible
                                color = Color.LightGray, // Light color for the divider
                            )
                        }
                    },
                )
            },

            content = { innerPadding ->
                Column(
                    modifier = Modifier
                        .padding(top = 10.dp)
                        .fillMaxHeight()
                        .fillMaxWidth(),
                ) {
                    Box(
                        modifier = Modifier
                            .background(Color(0xFFFAFAFA))
                            .fillMaxWidth()
                            .padding(
                                PaddingValues(
                                    start = innerPadding.calculateStartPadding(LayoutDirection.Ltr) + 16.dp,
                                    top = innerPadding.calculateTopPadding(),
                                    end = innerPadding.calculateEndPadding(LayoutDirection.Ltr) + 16.dp,
                                    bottom = innerPadding.calculateBottomPadding(),
                                ),
                            ),
                    ) {
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 20.dp),
                            shape = RoundedCornerShape(24.dp),
                            elevation = 4.dp,
                            color = MaterialTheme.colorScheme.surface,
                        ) {
                            TextField(
                                value = searchQuery,
                                onValueChange = { viewModel.onSearchQueryChanged(it) },
                                modifier = Modifier
                                    .heightIn(min = 5.dp)
                                    .fillMaxWidth(),
                                placeholder = {
                                    Text(
                                        "Search artists...",
                                        fontSize = 10.sp,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis,
                                    )
                                },
                                textStyle = TextStyle(fontSize = 12.sp), // Smaller input text
                                shape = RoundedCornerShape(10.dp),
                                colors = TextFieldDefaults.textFieldColors(
                                    backgroundColor = MaterialTheme.colorScheme.background,
                                    focusedIndicatorColor = Color.Transparent,
                                    unfocusedIndicatorColor = Color.Transparent,
                                    placeholderColor = MaterialTheme.colorScheme.onSurface.copy(
                                        alpha = 0.5f,
                                    ),
                                ),
                                leadingIcon = {
                                    Icon(
                                        imageVector = Icons.Default.Search,
                                        contentDescription = "Search",
                                        modifier = Modifier.size(18.dp),
                                        tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                                    )
                                },
                                trailingIcon = {
                                    if (searchQuery.isNotEmpty()) {
                                        IconButton(
                                            onClick = { viewModel.onSearchQueryChanged("") },
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Close,
                                                contentDescription = "Clear",
                                                modifier = Modifier.size(18.dp),
                                                tint = MaterialTheme.colorScheme.onSurface.copy(
                                                    alpha = 0.6f,
                                                ),
                                            )
                                        }
                                    }
                                },
                            )
                        }

                        // Content Area (scrollable below search bar)
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(top = 60.dp), // Add spacing between search and content
                        ) {
                            println("sfgg $errorMessage")
                            when {
                                isLoading -> { isDataLoading() } // isDataLoading()
                                filteredArtists.isNullOrEmpty() && searchQuery.isNotEmpty() -> Text(
                                    "No songs found",
                                    modifier = Modifier.padding(16.dp),
                                )

                                else ->
                                    if (errorMessage == null) {
                                        LazyVerticalGrid(
                                            columns = GridCells.Fixed(2), // 2 items per row
                                            horizontalArrangement = Arrangement.spacedBy(16.dp),
                                            verticalArrangement = Arrangement.spacedBy(30.dp),
                                            modifier = Modifier.fillMaxHeight(),
                                        ) {
                                            if (filteredArtists != null) {
                                                items(filteredArtists!!.size) { index ->
                                                    val sectionData = filteredArtists!![index]
                                                    ArtistCard(
                                                        sectionData,
                                                        navigationController,
                                                        selectedTrack,
                                                    )
                                                }
                                            }
                                        }
                                    } else {
                                        Text("Check your network")
                                    }
                            }
                        }
                    }
                }
            },
            bottomBar = {
                println("dfghdfxcvzsd fdsfdhh $errorMessage $selectedTrack")
                Box(modifier = Modifier.fillMaxWidth()) {
                }

                AnimatedVisibility(
                    visible = selectedTrack != null,
                    enter = slideInVertically(initialOffsetY = { fullHeight -> fullHeight }),
                ) {
                    println("dfghdfxcvz $errorMessage $selectedTrack")
                    selectedTrack?.let { track ->
                        BottomPlayerTab(
                            song = track,
                            songsViewModel,
                            onBottomTabClick = onBottomTabClick,
                        )
                    }
                }
            },
        )
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ArtistCard(
    artist: ArtistsItem,
    navigationController: NavHostController,
    selectedTrack: Songs? = null,
) {
    Card(
        modifier = Modifier
            .padding(12.dp)
            .aspectRatio(1.2f)
            .clickable {
                navigationController.navigate("${Screen.SongsList.route}/${artist.name}/${artist.id}")
            },
        shape = RoundedCornerShape(16.dp),
        elevation = 10.dp,
    ) {
        Box(
            modifier = Modifier.fillMaxSize().background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color.Transparent,
                        Color(0xAA000000), // Dark gradient for text visibility
                    ),
                    startY = 0f,
                    endY = 600f,
                ),
            ),
        ) {
            // Artist image with loading state
            AsyncImage(
                model = artist.imgurl,
                contentDescription = artist.name,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize(),
            )

            // Gradient overlay for text visibility
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                Color.Transparent,
                                Color.Black.copy(alpha = 0.9f),
                            ),
                            startY = 100f,
                            endY = 0.9f * Float.POSITIVE_INFINITY,
                        ),
                    ),
            ) {
                // Play button indicator
                if (selectedTrack != null) {
                    println("hjgjh $selectedTrack")
                    if (artist.id == selectedTrack.artist_id && (selectedTrack.state == MusicPlayerStates.STATE_PLAYING || selectedTrack.state == MusicPlayerStates.STATE_BUFFERING)) {
                        LottieAnimationForPlayingSong(
                            rememberLottieDynamicProperties(
                                LottieDynamicProperty(
                                    property = LottieProperty.COLOR,
                                    value = md_theme_light_primary.toArgb(),
                                    keyPath = KeyPath("**"),
                                ),
                            ),
                            modifier = Modifier.size(30.dp).background(Color.White, shape = RoundedCornerShape(50)).border(4.dp, Color.Transparent),
                        )
                    }
                }
            }

            // Artist name
            Text(
                text = artist.name,
                style = MaterialTheme.typography.labelSmall.copy(
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                ),
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(all = 8.dp)
                    .basicMarquee(), // Enables text scrolling
                maxLines = 1, // Ensure only one line,
                overflow = TextOverflow.Ellipsis,
            )
        }
    }
}
