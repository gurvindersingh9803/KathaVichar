package com.example.kathavichar.viewModel

import androidx.lifecycle.ViewModel
import com.example.kathavichar.model.ArtistsItem
import com.example.kathavichar.model.VersionInfo
import com.example.kathavichar.network.ServerResponse
import com.example.kathavichar.repositories.VersionRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.koin.java.KoinJavaComponent.inject
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SplashScreenViewModel : ViewModel() {
    private val _uiState: MutableStateFlow<ServerResponse<VersionInfo>> = MutableStateFlow(ServerResponse.isLoading())
    val uiState = _uiState.asStateFlow()
    private val versionRepository: VersionRepository by inject(VersionRepository::class.java)

    fun fetchVersionInfo(currentAppVersion: String) {
        viewModelScope.launch {
            try {
                withContext(Dispatchers.IO) {
                    versionRepository.getVersionInfo(currentAppVersion).let { versionInfo ->
                        when {
                            versionInfo.forceUpgrade -> {
                                // TODO: show alert to upgrade the app
                            }
                            versionInfo.needsUpgrade -> {
                                // TODO: show soft alert to upgrade the app
                                _uiState.tryEmit(ServerResponse.onSuccess(versionInfo))

                            }

                            else -> {
                                _uiState.tryEmit(ServerResponse.onSuccess(versionInfo))
                            }
                        }
                    }
                }
            }catch (e: Exception) {
                _uiState.tryEmit(ServerResponse.onError(data = null, message = e.toString()))
            }
        }
    }
}