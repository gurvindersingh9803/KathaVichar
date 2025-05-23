package com.musicplayer.kathavichar.view.composables.musicPlayer

import android.app.Activity
import android.content.Context
import android.os.Handler
import android.os.Looper
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.musicplayer.kathavichar.BuildConfig
import com.musicplayer.kathavichar.common.sharedComposables.NextIcon
import com.musicplayer.kathavichar.common.sharedComposables.PausePlayIcon
import com.musicplayer.kathavichar.common.sharedComposables.PreviousIcon
import com.musicplayer.kathavichar.common.sharedComposables.SongImage
import com.musicplayer.kathavichar.common.sharedComposables.SongName
import com.musicplayer.kathavichar.model.Songs
import com.musicplayer.kathavichar.repositories.musicPlayer.MusicPlayerEvents
import com.musicplayer.kathavichar.view.composables.songs.md_theme_light_primary
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import kotlinx.coroutines.delay

@Composable
fun BottomPlayerTab(
    song: Songs,
    musicPlayerEvents: MusicPlayerEvents,
    onBottomTabClick: () -> Unit,
) {
    println("ssygdefg $song")
    Column(
        Modifier.clickable { onBottomTabClick.invoke() }
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        md_theme_light_primary.copy(alpha = 1f),
                        md_theme_light_primary.copy(alpha = 0.7f),
                    ),
                ),
                shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp),
            )
            .padding(vertical = 4.dp),
    ) {
        Row(
            modifier =
            Modifier
                .clip(RoundedCornerShape(12.dp))
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            SongImage(songImage = song.imgurl.toString(), modifier = Modifier.size(70.dp))
            SongName(songName = song.title.toString(), modifier = Modifier.weight(1f))
            PreviousIcon(onClick = { musicPlayerEvents.onPreviousClicked(true, song) }, isBottomTab = true)
            PausePlayIcon(
                currentSong = song,
                onClick = { musicPlayerEvents.onPlayPauseClicked(song) },
                isBottomTab = true,
            )
            NextIcon(onClick = { musicPlayerEvents.onNextClicked(true, song) }, isBottomTab = true)
        }
    }
}

@Composable
fun AdBanner(
    modifier: Modifier = Modifier,
    onAdLoaded: () -> Unit = {},
    onAdFailed: () -> Unit = {},
) {
    var isAdLoaded by remember { mutableStateOf(false) }
    val adRefreshKey by remember { mutableStateOf(System.currentTimeMillis()) }

    LaunchedEffect(adRefreshKey) {
        while (true) {
            delay(60000) // Refresh every 60 seconds
            isAdLoaded = false
        }
    }

    AndroidView(
        modifier = modifier
            .fillMaxWidth()
            .height(if (isAdLoaded) 60.dp else 0.dp),
        factory = { context ->
            AdView(context).apply {
                setAdSize(AdSize.BANNER)
                adUnitId = BuildConfig.ADMOB_BANNERL_ID
                adListener = object : AdListener() {
                    override fun onAdLoaded() {
                        isAdLoaded = true
                        onAdLoaded()
                    }

                    override fun onAdFailedToLoad(error: LoadAdError) {
                        isAdLoaded = false
                        onAdFailed()
                        // Retry after failure
                        loadAd(AdRequest.Builder().build())
                    }

                    override fun onAdClosed() {
                        // Load new ad when current one is closed
                        loadAd(AdRequest.Builder().build())
                    }
                }

                loadAd(AdRequest.Builder().build())
            }
        },
        update = { adView ->
            if (!isAdLoaded) {
                adView.loadAd(AdRequest.Builder().build())
            }
        },
    )
}

class AdManager(private val context: Context) {
    private var interstitialAd: InterstitialAd? = null
    private var isLoading = false
    private var onAdLoaded: (() -> Unit)? = null

    init {
        // Set test device ID for test ads
       /* val testDeviceIds = listOf("37ae4d96-de54-4ad3-8186- f704a8ed6f73") // Replace with your test device ID
        val configuration = RequestConfiguration.Builder()
            .setTestDeviceIds(testDeviceIds)
            .build()
        MobileAds.setRequestConfiguration(configuration)*/
        loadInterstitialAd() // Load ad when AdManager is created
    }

    fun showInterstitialAd(activity: Activity) {
        println("Ad Status - interstitialAd: ${interstitialAd != null}, isLoading: $isLoading")

        if (interstitialAd != null) {
            interstitialAd?.show(activity)
            interstitialAd = null // Reset after showing to load a new ad
            loadInterstitialAd() // Preload the next ad
        } else {
            println("Ad not ready, trying to load...")
            loadInterstitialAd()
            // Queue the show request for when ad loads
            onAdLoaded = {
                println("Ad loaded, showing now")
                interstitialAd?.show(activity)
                interstitialAd = null // Reset after showing
                loadInterstitialAd() // Preload the next ad
                onAdLoaded = null
            }
        }
    }

    fun loadInterstitialAd() {
        if (isLoading || interstitialAd != null) {
            println("Skipping ad load - already loading: $isLoading or ad exists")
            return
        }

        isLoading = true
        println("Starting to load interstitial ad")

        InterstitialAd.load(
            context,
            BuildConfig.ADMOB_INTERSTITIAL_ID, // Replace with your ad unit ID
            AdRequest.Builder().build(),
            object : InterstitialAdLoadCallback() {
                override fun onAdLoaded(ad: InterstitialAd) {
                    println("Ad loaded successfully")
                    interstitialAd = ad
                    isLoading = false
                    onAdLoaded?.invoke()
                }

                override fun onAdFailedToLoad(error: LoadAdError) {
                    println("Ad failed to load: ${error.message}")
                    interstitialAd = null
                    isLoading = false
                    // Retry after 5 seconds
                    Handler(Looper.getMainLooper()).postDelayed({
                        loadInterstitialAd()
                    }, 5000)
                }
            },
        )
    }
}