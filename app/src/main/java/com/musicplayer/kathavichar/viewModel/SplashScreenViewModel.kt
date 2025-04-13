package com.musicplayer.kathavichar.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.musicplayer.kathavichar.common.AndroidNetworkStatusProvider
import com.musicplayer.kathavichar.model.VersionInfo
import com.musicplayer.kathavichar.network.ServerResponse
import com.musicplayer.kathavichar.repositories.VersionRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.java.KoinJavaComponent.inject
import kotlin.system.measureTimeMillis

class SplashScreenViewModel : ViewModel() {
    private val _uiState: MutableStateFlow<ServerResponse<VersionInfo>> = MutableStateFlow(ServerResponse.isLoading())
    val uiState = _uiState.asStateFlow()
    private val versionRepository: VersionRepository by inject(VersionRepository::class.java)
    private val networkStatusProvider: AndroidNetworkStatusProvider by inject(
        AndroidNetworkStatusProvider::class.java)
    val isNetworkAvailable: StateFlow<Boolean> = networkStatusProvider.networkStatusFlow

    fun fetchVersionInfo(currentAppVersion: String) {
        viewModelScope.launch {
            if(isNetworkAvailable.value) {
                try {
                    val versionInfo: VersionInfo
                    val elapsedTime = measureTimeMillis {
                        versionInfo = withContext(Dispatchers.IO) {
                            versionRepository.getVersionInfo(currentAppVersion)
                        }
                    }

                    // ⏱ Delay only if network was faster than splash duration
                    val minSplashDuration = 2000L
                    val delayTime = (minSplashDuration - elapsedTime).coerceAtLeast(0)
                    println("sfghdfghoop $delayTime")
                    delay(delayTime)

                    if(!versionInfo.forceUpgrade) {
                        println("mbvmvbbn")
                        _uiState.tryEmit(ServerResponse.onSuccess(versionInfo))
                    } else {
                        _uiState.tryEmit(
                            ServerResponse.onError(
                                data = null,
                                message = "A new version of the app is available. Please update to continue."
                            )
                        )
                    }

                } catch (e: Exception) {
                    delay(2000)
                    _uiState.tryEmit(
                        ServerResponse.onError(
                            data = null,
                            message = "Please try to restart the app. Check your network connection"
                        )
                    )
                }
            } else {
                delay(2000)
                _uiState.tryEmit(
                    ServerResponse.onError(
                        data = null,
                        message = "Network not available"
                    )
                )
            }
        }
    }

}
