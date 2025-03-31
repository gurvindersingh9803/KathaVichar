package com.example.kathavichar.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.kathavichar.model.ArtistsItem
import com.example.kathavichar.network.ServerResponse
import com.example.kathavichar.repositories.ArtistsDataRepository
import io.reactivex.disposables.CompositeDisposable
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.java.KoinJavaComponent.inject

class MainViewModel : ViewModel() {
    private val artistsDataRepository: ArtistsDataRepository by inject(ArtistsDataRepository::class.java)
    private val _uiState: MutableStateFlow<ServerResponse<List<ArtistsItem>>> = MutableStateFlow(ServerResponse.isLoading())
    val uiState = _uiState.asStateFlow()




    private val subscription: CompositeDisposable = CompositeDisposable()

    // Search state
    private val _searchQuery = MutableStateFlow("")
    val searchQuery = _searchQuery.asStateFlow()

    // Combined state for UI
    val filteredArtists: StateFlow<List<ArtistsItem>?> = combine(
        _uiState,
        _searchQuery
    ) { response, query ->
        when (response) {
            is ServerResponse.onSuccess -> {
                if (query.isEmpty()) {
                    response.data
                } else {
                    response.data?.filter { artist ->
                        artist.name.contains(query, ignoreCase = true) == true
                    }
                }
            }
            else -> null
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = null
    )

    fun onSearchQueryChanged(query: String) {
        _searchQuery.value = query
    }

    fun getCategories() {
        viewModelScope.launch {
            // delay(2000)
            withContext(Dispatchers.Main) {
                artistsDataRepository.fetchArtists().let {
                    println("retyeter $it")
                    _uiState.emit(ServerResponse.onSuccess(it))
                }
            }
        }
    }


    override fun onCleared() {
        super.onCleared()
        println("onCleared")
        subscription.clear()
    }
}
