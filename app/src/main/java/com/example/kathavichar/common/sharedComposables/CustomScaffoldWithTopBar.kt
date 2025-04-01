package com.example.kathavichar.common.sharedComposables

import android.provider.CalendarContract.Colors
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.kathavichar.view.composables.songs.typography
import java.nio.file.WatchEvent.Modifier

class CustomScaffoldWithTopBar {
}

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
    actions: @Composable RowScope.() -> Unit = {},
    content: @Composable (PaddingValues) -> Unit
) {
        Scaffold(
            topBar = {
                if(shouldVisible) {
                    Surface(shadowElevation = 0.dp) {
                        Column {
                        TopAppBar(
                            title = {
                                Text(
                                    text = title, // Makes text uppercase for a modern look
                                    style = typography.labelLarge.copy(
                                        color = Color.Black, // Pure black color
                                        fontWeight = FontWeight.Bold, // Bold for emphasis
                                        fontSize = 32.sp, // Increased font size
                                        letterSpacing = 1.1.sp, // Better spacing for clarity
                                        shadow = Shadow( // Subtle shadow for depth
                                            color = Color.Gray.copy(alpha = 0.3f),
                                            offset = Offset(1f, 1f),
                                            blurRadius = 3f
                                        )
                                    ),
                                    modifier = androidx.compose.ui.Modifier.padding(horizontal = 14.dp)
                                )

                            },
                            actions = actions,
                        )
                        }
                    }
                }
            },
            content = content
        )

}
