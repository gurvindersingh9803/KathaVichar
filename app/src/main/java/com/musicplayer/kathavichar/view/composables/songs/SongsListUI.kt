package com.musicplayer.kathavichar.view.composables.songs

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.view.WindowCompat
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieDynamicProperties
import com.airbnb.lottie.compose.rememberLottieComposition
import com.musicplayer.kathavichar.R
import com.musicplayer.kathavichar.model.Songs
import com.musicplayer.kathavichar.repositories.musicPlayer.MusicPlayerStates
import com.musicplayer.kathavichar.view.composables.musicPlayer.TrackImage
import com.musicplayer.kathavichar.viewModel.SongsViewModel

@Composable
fun SongsListUI(
    songsViewModel: SongsViewModel,
    innerPadding: PaddingValues,
    artistId: String?,
    artistName: String?,
) {
    val lazyListState = rememberLazyListState()

    val songs = songsViewModel.songs
/*
    val trackDuration by songsViewModel.playbackState.collectAsState()
*/

    val searchQuery by songsViewModel.searchQuery.collectAsState()
    val filteredSongs by songsViewModel.filteredSongs.collectAsState()
    LaunchedEffect(Unit) {
        // songsViewModel.observePlaybackState()
    }
    // Prepare and play the media when the player is set up
    /*LaunchedEffect(key1 = isPlayerSetUp)main
        if (isPlayerSetUp) {
            mediaController?.run {
                if (mediaItemCount > 0) {
                    println("deghdsgh $mediaItemCount")
                    prepare()
                    play()
                }
            }
        }
    }*/

    Column(
        modifier = Modifier
            .fillMaxWidth(),

    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    PaddingValues(
                        start = innerPadding.calculateStartPadding(LayoutDirection.Ltr) + 16.dp,
                        top = innerPadding.calculateTopPadding(),
                        end = innerPadding.calculateEndPadding(LayoutDirection.Ltr) + 16.dp,
                        bottom = 0.dp,
                    ),
                ),

        ) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp, bottom = 5.dp),
                shape = RoundedCornerShape(24.dp),
                elevation = 4.dp,
                color = MaterialTheme.colorScheme.surface,
            ) {
                TextField(
                    value = searchQuery,
                    onValueChange = { songsViewModel.onSearchQueryChanged(it) },
                    modifier = Modifier
                        .heightIn(min = 50.dp),
                    placeholder = {
                        Text(
                            "Search songs...",
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
                            tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                        )
                    },
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(
                                onClick = { songsViewModel.onSearchQueryChanged("") },
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = "Clear",
                                    tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                                )
                            }
                        }
                    },
                )
            }
        }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(0.dp), // Add spacing between search and content
        ) {
            when {
                filteredSongs.isEmpty() && searchQuery.isNotEmpty() -> Text(
                    "No songs found",
                    modifier = Modifier.padding(16.dp),
                )
                else -> {
                    LazyColumn(
                        modifier = Modifier.padding(16.dp),
                    ) {
                        filteredSongs.let {
                            items(it.size) { index ->
                                SongItem(
                                    song = filteredSongs[index],
                                    onTrackClick = {
                                        songsViewModel.onTrackClicked(filteredSongs[index])
                                    },
                                    artistId,
                                    artistName,
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SongItem(
    song: Songs,
    onTrackClick: () -> Unit,
    artistId: String?,
    artistName: String?,
) {
    println("kmhnbmnm $song")
    val bgColor = if (song.isSelected) md_theme_light_primary else md_theme_light_surfaceVariant
    val textColor =
        if (song.isSelected) md_theme_light_onPrimary else md_theme_light_onSurfaceVariant
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier =
        Modifier
            .padding(top = 5.dp)
            .clip(shape = RoundedCornerShape(8.dp))
            .background(color = bgColor)
            .clickable(onClick = onTrackClick),
    ) {
        TrackImage(trackImage = song.imgurl.toString(), modifier = Modifier.size(size = 64.dp))
        Column(
            modifier =
            Modifier
                .padding(start = 10.dp, end = 10.dp)
                .weight(weight = 1f),
        ) {
            Text(text = song.title, style = typography.bodyLarge, color = textColor)
            Spacer(modifier = Modifier.height(5.dp))
            Text(text = artistName.toString(), style = typography.bodySmall, color = textColor)
            Text(text = song.formattedDuration.toString(), style = typography.bodySmall, color = textColor)
        }
        if (song.state == MusicPlayerStates.STATE_PLAYING) LottieAnimationForPlayingSong(modifier = Modifier.size(64.dp))
        if (song.state == MusicPlayerStates.STATE_BUFFERING) {
            CircularProgressIndicator(
                color = Color.White,
                strokeWidth = 2.dp,
                modifier = Modifier
                    .size(50.dp)
                    .padding(12.dp), // Explicit size for better control
            )
        }
    }
}

@Composable
fun LottieAnimationForPlayingSong(dynamicProperties: LottieDynamicProperties? = null, modifier: Modifier?) {
    val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.audio_wave))
    if (modifier != null) {
        LottieAnimation(
            modifier = modifier,
            composition = composition,
            iterations = Int.MAX_VALUE,
            dynamicProperties = dynamicProperties,
        )
    }
}

/*val md_theme_light_primary = Color(0xFF00b59a)
val md_theme_light_onPrimary = Color(0xFFFFFFFF)
val md_theme_light_primaryContainer = Color(0xFF45FDDA)
val md_theme_light_onPrimaryContainer = Color(0xFF00201A)
val md_theme_light_secondary = Color(0xFF4B635C)
val md_theme_light_onSecondary = Color(0xFFFFFFFF)
val md_theme_light_secondaryContainer = Color(0xFFCDE8DF)
val md_theme_light_onSecondaryContainer = Color(0xFF06201A)
val md_theme_light_tertiary = Color(0xFF436278)
val md_theme_light_onTertiary = Color(0xFFFFFFFF)
val md_theme_light_tertiaryContainer = Color(0xFFC8E7FF)
val md_theme_light_onTertiaryContainer = Color(0xFF001E2E)
val md_theme_light_error = Color(0xFFBA1A1A)
val md_theme_light_errorContainer = Color(0xFFFFDAD6)
val md_theme_light_onError = Color(0xFFFFFFFF)
val md_theme_light_onErrorContainer = Color(0xFF410002)
val md_theme_light_background = Color(0xFFFAFDFA)
val md_theme_light_onBackground = Color(0xFF191C1B)
val md_theme_light_surface = Color(0xFFFAFDFA)
val md_theme_light_onSurface = Color(0xFF191C1B)
val md_theme_light_surfaceVariant = Color(0xFFe5fff6)
val md_theme_light_onSurfaceVariant = Color(0xFF3F4946)
val md_theme_light_outline = Color(0xFF6F7975)
val md_theme_light_inverseOnSurface = Color(0xFFEFF1EF)
val md_theme_light_inverseSurface = Color(0xFF2E3130)
val md_theme_light_inversePrimary = Color(0xFF00DFBF)
val md_theme_light_surfaceTint = Color(0xFF006B5A)
val md_theme_light_outlineVariant = Color(0xFFBFC9C4)
val md_theme_light_scrim = Color(0xFF000000)*/

val md_theme_dark_primary = Color(0xFF00DFBF)
val md_theme_dark_onPrimary = Color(0xFF00382E)
val md_theme_dark_primaryContainer = Color(0xFF005144)
val md_theme_dark_onPrimaryContainer = Color(0xFF45FDDA)
val md_theme_dark_secondary = Color(0xFFB1CCC4)
val md_theme_dark_onSecondary = Color(0xFF1D352F)
val md_theme_dark_secondaryContainer = Color(0xFF334B45)
val md_theme_dark_onSecondaryContainer = Color(0xFFCDE8DF)
val md_theme_dark_tertiary = Color(0xFFAACBE4)
val md_theme_dark_onTertiary = Color(0xFF113447)
val md_theme_dark_tertiaryContainer = Color(0xFF2A4A5F)
val md_theme_dark_onTertiaryContainer = Color(0xFFC8E7FF)
val md_theme_dark_error = Color(0xFFFFB4AB)
val md_theme_dark_errorContainer = Color(0xFF93000A)
val md_theme_dark_onError = Color(0xFF690005)
val md_theme_dark_onErrorContainer = Color(0xFFFFDAD6)
val md_theme_dark_background = Color(0xFF191C1B)
val md_theme_dark_onBackground = Color(0xFFE0E3E0)
val md_theme_dark_surface = Color(0xFF191C1B)
val md_theme_dark_onSurface = Color(0xFFE0E3E0)
val md_theme_dark_surfaceVariant = Color(0xFF3F4946)
val md_theme_dark_onSurfaceVariant = Color(0xFFBFC9C4)
val md_theme_dark_outline = Color(0xFF89938F)
val md_theme_dark_inverseOnSurface = Color(0xFF191C1B)
val md_theme_dark_inverseSurface = Color(0xFFE0E3E0)
val md_theme_dark_inversePrimary = Color(0xFF006B5A)
val md_theme_dark_surfaceTint = Color(0xFF00DFBF)
val md_theme_dark_outlineVariant = Color(0xFF3F4946)
val md_theme_dark_scrim = Color(0xFF000000)

val md_theme_light_primary = Color(0xFFFF8A4C) // Primary orange
val md_theme_light_onPrimary = Color(0xFFFFFFFF) // White text on primary
val md_theme_light_primaryContainer = Color(0xFFFFD180) // Lighter orange container
val md_theme_light_onPrimaryContainer = Color(0xFF4E2600) // Dark brown text

val md_theme_light_secondary = Color(0xFFFFAB40) // Secondary orange (lighter shade)
val md_theme_light_onSecondary = Color(0xFF4E2600) // Dark text on secondary
val md_theme_light_secondaryContainer = Color(0xFFFFE0B2) // Even lighter orange container
val md_theme_light_onSecondaryContainer = Color(0xFF3E1A00) // Very dark brown text

val md_theme_light_tertiary = Color(0xFFFB8C00) // Slightly different tone for tertiary
val md_theme_light_onTertiary = Color(0xFFFFFFFF) // White text on tertiary
val md_theme_light_tertiaryContainer = Color(0xFFFFCC80) // Light orange container
val md_theme_light_onTertiaryContainer = Color(0xFF4A1D00) // Dark brown text

val md_theme_light_error = Color(0xFFD32F2F) // Red for errors
val md_theme_light_errorContainer = Color(0xFFFFCDD2) // Light red container
val md_theme_light_onError = Color(0xFFFFFFFF) // White text on error
val md_theme_light_onErrorContainer = Color(0xFF680000) // Dark red text

val md_theme_light_background = Color(0xFFFFF3E0) // Very light orange background
val md_theme_light_onBackground = Color(0xFF4E342E) // Dark brown text on background
val md_theme_light_surface = Color(0xFFFFF8E1) // Light cream surface
val md_theme_light_onSurface = Color(0xFF5D4037) // Medium brown text on surface

val md_theme_light_surfaceVariant = Color(0xFFFEECE2) // Light variant of surface
val md_theme_light_onSurfaceVariant = Color(0xFF3E2723) // Darker variant for text
val md_theme_light_outline = Color(0xFF8D6E63) // Outline color (light brown)
val md_theme_light_inverseOnSurface = Color(0xFFFFF8E1) // Inverse light cream text
val md_theme_light_inverseSurface = Color(0xFF3E2723) // Inverse dark brown surface
val md_theme_light_inversePrimary = Color(0xFFFFB74D) // Light variant of primary
val md_theme_light_surfaceTint = Color(0xFFFF9800) // Surface tint using primary
val md_theme_light_outlineVariant = Color(0xFFBCAAA4) // Lighter outline variant
val md_theme_light_scrim = Color(0xFF000000) // Scrim color (black for overlays)

val typography =
    Typography(
        bodyLarge =
        TextStyle(
            fontFamily = FontFamily(Font(R.font.latobold)),
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp,
        ),
        bodySmall =
        TextStyle(
            fontFamily = FontFamily(Font(R.font.latoregular)),
            fontWeight = FontWeight.Normal,
            fontSize = 13.sp,
        ),
        titleLarge =
        TextStyle(
            fontFamily = FontFamily(Font(R.font.latolight)),
            fontWeight = FontWeight.Bold,
            fontSize = 20.sp,
            color = md_theme_light_onPrimary,
        ),
    )

/**
 * Light color scheme used for the app's light theme.
 * Each color property is named according to its use in the UI.
 */
private val _lightColors =
    lightColorScheme(
        primary = md_theme_light_primary,
        onPrimary = md_theme_light_onPrimary,
        primaryContainer = md_theme_light_primaryContainer,
        onPrimaryContainer = md_theme_light_onPrimaryContainer,
        secondary = md_theme_light_secondary,
        onSecondary = md_theme_light_onSecondary,
        secondaryContainer = md_theme_light_secondaryContainer,
        onSecondaryContainer = md_theme_light_onSecondaryContainer,
        tertiary = md_theme_light_tertiary,
        onTertiary = md_theme_light_onTertiary,
        tertiaryContainer = md_theme_light_tertiaryContainer,
        onTertiaryContainer = md_theme_light_onTertiaryContainer,
        error = md_theme_light_error,
        errorContainer = md_theme_light_errorContainer,
        onError = md_theme_light_onError,
        onErrorContainer = md_theme_light_onErrorContainer,
        background = md_theme_light_background,
        onBackground = md_theme_light_onBackground,
        surface = md_theme_light_surface,
        onSurface = md_theme_light_onSurface,
        surfaceVariant = md_theme_light_surfaceVariant,
        onSurfaceVariant = md_theme_light_onSurfaceVariant,
        outline = md_theme_light_outline,
        inverseOnSurface = md_theme_light_inverseOnSurface,
        inverseSurface = md_theme_light_inverseSurface,
        inversePrimary = md_theme_light_inversePrimary,
        surfaceTint = md_theme_light_surfaceTint,
        outlineVariant = md_theme_light_outlineVariant,
        scrim = md_theme_light_scrim,
    )

/**
 * Dark color scheme used for the app's dark theme.
 * Each color property is named according to its use in the UI.
 */
private val _darkColors =
    darkColorScheme(
        primary = md_theme_dark_primary,
        onPrimary = md_theme_dark_onPrimary,
        primaryContainer = md_theme_dark_primaryContainer,
        onPrimaryContainer = md_theme_dark_onPrimaryContainer,
        secondary = md_theme_dark_secondary,
        onSecondary = md_theme_dark_onSecondary,
        secondaryContainer = md_theme_dark_secondaryContainer,
        onSecondaryContainer = md_theme_dark_onSecondaryContainer,
        tertiary = md_theme_dark_tertiary,
        onTertiary = md_theme_dark_onTertiary,
        tertiaryContainer = md_theme_dark_tertiaryContainer,
        onTertiaryContainer = md_theme_dark_onTertiaryContainer,
        error = md_theme_dark_error,
        errorContainer = md_theme_dark_errorContainer,
        onError = md_theme_dark_onError,
        onErrorContainer = md_theme_dark_onErrorContainer,
        background = md_theme_dark_background,
        onBackground = md_theme_dark_onBackground,
        surface = md_theme_dark_surface,
        onSurface = md_theme_dark_onSurface,
        surfaceVariant = md_theme_dark_surfaceVariant,
        onSurfaceVariant = md_theme_dark_onSurfaceVariant,
        outline = md_theme_dark_outline,
        inverseOnSurface = md_theme_dark_inverseOnSurface,
        inverseSurface = md_theme_dark_inverseSurface,
        inversePrimary = md_theme_dark_inversePrimary,
        surfaceTint = md_theme_dark_surfaceTint,
        outlineVariant = md_theme_dark_outlineVariant,
        scrim = md_theme_dark_scrim,
    )

/**
 * A composable function that wraps the provided content within a Material Theme.
 * It applies either a light or dark color scheme based on the system settings and Android version.
 *
 * @param darkTheme A boolean representing whether the dark theme should be applied. Default is based on the system settings.
 * @param dynamicColor A boolean representing whether dynamic color should be applied. Dynamic color is available on Android 12+. Default is true.
 * @param content The content to be drawn within this theme.
 */
@Composable
fun MusicPlayerJetpackComposeTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit,
) {
    val colorScheme =
        when {
            dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
                val context = LocalContext.current
                if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
            }

            darkTheme -> _darkColors
            else -> _lightColors
        }
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.primary.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = com.musicplayer.kathavichar.view.composables.songs.typography,
        content = content,
    )
}
