package com.example.kathavichar.viewModel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.kathavichar.model.Song
import com.example.kathavichar.network.ServerResponse
import com.example.kathavichar.repositories.SongsListFirebase
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.koin.java.KoinJavaComponent

class SongsViewModel : ViewModel() {

    private val _uiStateSongs: MutableStateFlow<ServerResponse<MutableList<Song>>> = MutableStateFlow(ServerResponse.isLoading())
    val uiStateSongs = _uiStateSongs.asStateFlow()
    private val sonsListFirebase: SongsListFirebase by KoinJavaComponent.inject(SongsListFirebase::class.java)

    val subscription: CompositeDisposable = CompositeDisposable()

    fun getSongs(artistName: String) {
        viewModelScope.launch {
            subscription.add(
                sonsListFirebase.getSongsList(artistName)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe({
                        Log.i("sdghdsfsongs", it.toString())
                        viewModelScope.launch {
                            _uiStateSongs.emit(ServerResponse.onSuccess(it))
                        }
                    }, {
                        Log.i("edfgwegf", it.toString())
                    }),
            )
        }
    }

    override fun onCleared() {
        super.onCleared()
        subscription.clear()
    }
}
