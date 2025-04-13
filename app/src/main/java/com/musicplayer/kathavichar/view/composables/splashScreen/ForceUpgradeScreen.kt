package com.musicplayer.kathavichar.view.composables.splashScreen

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.musicplayer.kathavichar.R
import com.musicplayer.kathavichar.view.composables.songs.md_theme_light_primary

@Composable
fun ForceUpgradeScreen(
    upgradeUrl: String,
    message: String = ""
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(32.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            if (message == "Network not available") {
                Image(
                    painter = painterResource(id = R.drawable.nowifi),
                    contentDescription = "No Network",
                )
            } else {
                Image(
                    painter = painterResource(id = R.drawable.warning),
                    modifier = Modifier.size(100.dp),
                    contentDescription = "No Network",
                )
            }
            Text(text = message, fontSize = 16.sp, textAlign = TextAlign.Center, color = Color.DarkGray)

            if (message != "Network not available")
            Button(
                onClick = {
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(upgradeUrl))
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    // LocalContext.current.startActivity(intent)
                },
                colors = ButtonDefaults.buttonColors(containerColor = md_theme_light_primary)
            ) {
                Text("Update Now", color = Color.White)
            }
        }
    }
}
