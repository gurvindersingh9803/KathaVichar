package com.example.kathavichar.viewModel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.kathavichar.model.Category
import com.example.kathavichar.network.ServerResponse
import com.example.kathavichar.repositories.FirebaseTestRepo
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.koin.java.KoinJavaComponent.inject

class MainViewModel : ViewModel() {

    private val firebaseTestRepo: FirebaseTestRepo by inject(FirebaseTestRepo::class.java)
    private val _uiState: MutableStateFlow<ServerResponse<List<Category>>> = MutableStateFlow(ServerResponse.isLoading())
    val uiState: StateFlow<ServerResponse<List<Category>>> = _uiState.asStateFlow()
    val subscription: CompositeDisposable = CompositeDisposable()

    init {
        viewModelScope.launch {
            getCategories()
        }
    }

    private fun getCategories() {
        viewModelScope.launch {
            subscription.add(
                firebaseTestRepo.getdata()
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe({
                        viewModelScope.launch {
                            Log.i("werfqwef", it.toString())
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
