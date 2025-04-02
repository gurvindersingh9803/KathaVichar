package com.example.kathavichar.common.sharedComposables

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.kathavichar.view.composables.songs.typography

class CustomScaffoldWithTopBar

/**
 * Reusable Scaffold with TopAppBar component
 * @param title The title to display in the TopAppBar
 * @param actions Optional actions to display in the TopAppBar
 * @param content The main content of the screen
 */

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScaffoldWithTopBar(
    shouldVisible: Boolean = true,
    title: String,
    actions: @Composable() (RowScope.() -> Unit) = {},
    content: @Composable (PaddingValues) -> Unit,
    bottomBar: @Composable () -> Unit,
) {
    Scaffold(
        topBar = {
            if (shouldVisible) {
                Surface(shadowElevation = 0.dp) {
                    TopAppBar(
                        title = {
                            Text(
                                text = title, // Makes text uppercase for a modern look
                                style = typography.titleMedium .copy(
                                    color = Color.Black, // Pure black color
                                    fontWeight = FontWeight.Light, // Bold for emphasis
                                    fontSize = 26.sp, // Increased font size
                                    letterSpacing = 1.0.sp, // Better spacing for clarity
                                    shadow = Shadow( // Subtle shadow for depth
                                        color = Color.Gray.copy(alpha = 0.6f),
                                        offset = Offset(1f, 1f),
                                        blurRadius = 8f,
                                    ),
                                ),
                                modifier = androidx.compose.ui.Modifier.padding(horizontal = 14.dp),
                            )
                        },
                        actions = actions,
                    )
                }
            }
        },
        bottomBar = bottomBar,
        content = content
    )
}
