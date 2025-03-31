package com.example.kathavichar.common.sharedComposables

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.RowScope
import androidx.compose.material.MaterialTheme
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.kathavichar.view.composables.songs.typography

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
                    Surface(shadowElevation = 5.dp) {
                        TopAppBar(
                            title = {
                                Text(
                                    text = title,
                                    style = typography.titleLarge,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            },
                            actions = actions,
                        )
                    }
                }
            },
            content = content
        )

}
