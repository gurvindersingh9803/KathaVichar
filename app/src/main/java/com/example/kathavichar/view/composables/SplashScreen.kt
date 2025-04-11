package com.example.kathavichar.view.composables

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.kathavichar.R
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(navController: NavController, isLoading: Boolean) {

    println("wfwervdc $isLoading")
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White),
            contentAlignment = Alignment.Center,
        ) {
            Image(
                painter = painterResource(id = R.drawable.main_img),
                contentDescription = "App Logo",
                modifier = Modifier.size(200.dp),
            )
        }

    LaunchedEffect(isLoading) {
        if (!isLoading) {
            navController.navigate("MainPlayList") {
                popUpTo("SplashScreen") { inclusive = true }
            }
        }
    }
}