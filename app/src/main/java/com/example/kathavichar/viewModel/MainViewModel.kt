package com.example.kathavichar.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.kathavichar.model.ArtistsItem
import com.example.kathavichar.network.ServerResponse
import com.example.kathavichar.repositories.ArtistsDataRepository
import com.example.kathavichar.repositories.HomeCategoriesFirebase
import io.reactivex.disposables.CompositeDisposable
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.java.KoinJavaComponent.inject

class MainViewModel : ViewModel() {
    private val homeCategoriesFirebase: HomeCategoriesFirebase by inject(HomeCategoriesFirebase::class.java)

    private val artistsDataRepository: ArtistsDataRepository by inject(ArtistsDataRepository::class.java)
    private val _uiState: MutableStateFlow<ServerResponse<List<ArtistsItem>>> = MutableStateFlow(ServerResponse.isLoading())
    val uiState = _uiState.asStateFlow()

    private val subscription: CompositeDisposable = CompositeDisposable()

    /*fun getCategories() {
        viewModelScope.launch {
            delay(2000)
            subscription.add(
                homeCategoriesFirebase
                    .getdata()
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe({
                        viewModelScope.launch {
                            println("dfgvdsfg $it")
                            _uiState.emit(ServerResponse.onSuccess(it))
                        }
                    }, {
                    }),
            )
        }
    }*/

    fun getCategories() {
        viewModelScope.launch {
            delay(2000)
            withContext(Dispatchers.IO) {
                artistsDataRepository.fetchArtists().let {
                    println("dfgvdsfg $it")
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
