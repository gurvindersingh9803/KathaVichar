package com.example.kathavichar.viewModel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.kathavichar.model.SectionData
import com.example.kathavichar.model.Song
import com.example.kathavichar.network.ServerResponse
import com.example.kathavichar.repositories.HomeCategoriesFirebase
import com.example.kathavichar.repositories.SongsListFirebase
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.koin.java.KoinJavaComponent.inject

class MainViewModel : ViewModel() {

    private val homeCategoriesFirebase: HomeCategoriesFirebase by inject(HomeCategoriesFirebase::class.java)

    private val _uiState: MutableStateFlow<ServerResponse<MutableList<SectionData>>> = MutableStateFlow(ServerResponse.isLoading())
    val uiState = _uiState.asStateFlow()

    private val subscription: CompositeDisposable = CompositeDisposable()

    fun getCategories() {
        viewModelScope.launch {
            subscription.add(
                homeCategoriesFirebase.getdata()
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe({
                        viewModelScope.launch {
                            _uiState.emit(ServerResponse.onSuccess(it))
                        }
                    }, {
                    }),
            )
        }
    }

    override fun onCleared() {
        super.onCleared()
        subscription.clear()
    }
}
