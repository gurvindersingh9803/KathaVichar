package com.example.kathavichar.view.composables.musicPlayer

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.ContentAlpha
import androidx.compose.material.Icon
import androidx.compose.material.LocalContentAlpha
import androidx.compose.material.MaterialTheme
import androidx.compose.material.ProgressIndicatorDefaults
import androidx.compose.material.Slider
import androidx.compose.material.SliderDefaults
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material.icons.rounded.NavigateNext
import androidx.compose.material.icons.rounded.PauseCircleFilled
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.kathavichar.R

@Composable
fun MusicPlayer() {
    Box(modifier = Modifier.fillMaxSize()) {
        Surface(modifier = Modifier.fillMaxSize()) {
            val gradient = Brush.verticalGradient(listOf(MaterialTheme.colors.primary, MaterialTheme.colors.primaryVariant))

            val sliderColors = SliderDefaults.colors(
                thumbColor = MaterialTheme.colors.background,
                activeTrackColor = MaterialTheme.colors.background,
                inactiveTrackColor = MaterialTheme.colors.background.copy(
                    alpha = ProgressIndicatorDefaults.IndicatorBackgroundOpacity,
                ),
            )

            Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center, modifier = Modifier.background(gradient)) {
                Box(
                    modifier = Modifier
                        .clip(MaterialTheme.shapes.medium)
                        .aspectRatio(1f),
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Image(painter = painterResource(id = R.drawable.imag), contentDescription = null, modifier = Modifier.fillMaxWidth())

                        Text(text = "Dasam Granth", style = MaterialTheme.typography.subtitle1, color = MaterialTheme.colors.surface)
                        Text(text = "Subtitle", style = MaterialTheme.typography.body2, color = MaterialTheme.colors.surface)

                        Slider(value = 0f, onValueChange = {}, colors = sliderColors)

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            CompositionLocalProvider(LocalContentAlpha provides ContentAlpha.medium) {
                                Text(
                                    "3:30",
                                    style = MaterialTheme.typography.body2,
                                )
                            }
                            CompositionLocalProvider(LocalContentAlpha provides ContentAlpha.medium) {
                                Text(
                                    "00.10",
                                    style = MaterialTheme.typography.body2,
                                )
                            }
                        }

                        Row(
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.ArrowBack,
                                contentDescription = "Skip Previous",
                                modifier = Modifier
                                    .clip(CircleShape)
                                    .clickable(onClick = {})
                                    .padding(12.dp)
                                    .size(48.dp),
                            )

                            Icon(
                                imageVector = Icons.Rounded.PauseCircleFilled,
                                contentDescription = "Skip Previous",
                                modifier = Modifier
                                    .clip(CircleShape)
                                    .clickable(onClick = {})
                                    .padding(12.dp)
                                    .size(62.dp),
                            )

                            Icon(
                                imageVector = Icons.Rounded.NavigateNext,
                                contentDescription = "Skip Previous",
                                modifier = Modifier
                                    .clip(CircleShape)
                                    .clickable(onClick = {})
                                    .padding(12.dp)
                                    .size(48.dp),
                            )
                        }
                    }
                }
            }
        }
    }
}
