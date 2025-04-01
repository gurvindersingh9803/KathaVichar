package com.example.kathavichar.view.composables.home

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.example.kathavichar.common.Screen
import com.example.kathavichar.common.sharedComposables.ScaffoldWithTopBar
import com.example.kathavichar.common.utils.isDataLoading
import com.example.kathavichar.model.ArtistsItem
import com.example.kathavichar.network.ServerResponse
import com.example.kathavichar.viewModel.MainViewModel

@Composable
fun ArtistSearchScreen(
    artists: List<ArtistsItem>?,
    navigationController: NavHostController,
    innerPadding: PaddingValues,
    viewModel: MainViewModel,
) {
    val filteredArtists by viewModel.filteredArtists.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val isLoading = viewModel.uiState.collectAsState().value is ServerResponse.isLoading

    ScaffoldWithTopBar(
        true,
        title = "Artists",
        actions = {
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxHeight().fillMaxWidth(),
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(PaddingValues(
                        start = innerPadding.calculateStartPadding(LayoutDirection.Ltr) + 16.dp,
                        top = innerPadding.calculateTopPadding(),
                        end = innerPadding.calculateEndPadding(LayoutDirection.Ltr) + 16.dp,
                        bottom = innerPadding.calculateBottomPadding()
                    )),

                ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(40.dp)
                        .background(
                            color = MaterialTheme.colorScheme.background, // Mimics Surface background
                            shape = RoundedCornerShape(10.dp)
                        )
                        .shadow(
                            elevation = 5.dp,
                            shape = RoundedCornerShape(10.dp)
                        )
                        .border(
                            width = 0.dp,
                            color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f),
                            shape = RoundedCornerShape(10.dp)
                        )
                ) {
                    TextField(
                        value = searchQuery,
                        onValueChange = { viewModel.onSearchQueryChanged(it) },
                        modifier = Modifier
                            .fillMaxWidth(),
                        placeholder = {
                            Text(
                                "Search artists...",
                                fontSize = 10.sp,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        },
                        textStyle = TextStyle(fontSize = 12.sp), // Smaller input text
                        shape = RoundedCornerShape(10.dp),
                        colors = TextFieldDefaults.textFieldColors(
                            backgroundColor = MaterialTheme.colorScheme.background,
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent,
                            placeholderColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
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
                                        tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
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
                        .padding(top = 90.dp), // Add spacing between search and content
                ) {
                    when {
                        isLoading -> isDataLoading()
                        filteredArtists.isNullOrEmpty() && searchQuery.isNotEmpty() -> Text(
                            "No songs found",
                            modifier = Modifier.padding(16.dp),
                        )

                        else -> LazyVerticalGrid(
                            columns = GridCells.Fixed(2), // 2 items per row
                            horizontalArrangement = Arrangement.spacedBy(16.dp),
                            verticalArrangement = Arrangement.spacedBy(30.dp),
                            modifier = Modifier.fillMaxHeight()
                        ) {
                            if (filteredArtists != null) {
                                items(20) { index ->
                                    val sectionData = filteredArtists!![0]
                                    ArtistCard(sectionData, navigationController)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ArtistCard(
    artist: ArtistsItem,
    navigationController: NavHostController,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier
            .fillMaxWidth(0.5f)
            .aspectRatio(1.4f)
            .clickable {
                navigationController.navigate("${Screen.SongsList.route}/${artist.id}")
            },
        shape = RoundedCornerShape(16.dp),
        elevation = 6.dp,
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
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
                            startY =  100f,
                            endY = 0.9f* Float.POSITIVE_INFINITY
                        ),
                    ),
            )

            // Play button indicator
            IconButton(
                onClick = { /* Handle play artist */ },
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(8.dp)
                    .background(
                        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.9f),
                        shape = CircleShape,
                    )
                    .size(36.dp),
            ) {
                Icon(
                    imageVector = Icons.Default.PlayArrow,
                    contentDescription = "Play artist",
                    tint = Color.White,
                    modifier = Modifier.size(18.dp),
                )
            }

            // Artist name
            Text(
                text = artist.name,
                style = MaterialTheme.typography.labelSmall.copy(
                    color = Color.White,
                    fontWeight = FontWeight.Normal,
                ),
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(all = 8.dp)
                    .basicMarquee(), // Enables text scrolling
                maxLines = 1, // Ensure only one line,
                overflow = TextOverflow.Clip,
            )
        }


    }
}
