package com.example.kathavichar.common.utils

import androidx.compose.material.TopAppBar
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

@Composable
fun KathaVicharTopBar() {
    TopAppBar(title = { Text("Katha Veechar", color = Color.White) }, backgroundColor = Color(0xff0f9d58))
}
