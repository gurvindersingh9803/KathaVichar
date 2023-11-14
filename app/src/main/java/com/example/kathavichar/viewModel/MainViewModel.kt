package com.example.kathavichar.viewModel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.kathavichar.model.Category
import com.example.kathavichar.network.ServerResponse
import com.example.kathavichar.repositories.FirebaseTestRepo
import kotlinx.coroutines.launch
import org.koin.java.KoinJavaComponent.inject

class MainViewModel : ViewModel() {

    private val firebaseTestRepo: FirebaseTestRepo by inject(FirebaseTestRepo::class.java)
    var isLoading = MutableLiveData(false)
    var categories = MutableLiveData<List<Category>?>(emptyList())
    var isErrorExist = MutableLiveData(false)

    init {
        viewModelScope.launch {
            firebaseTestRepo.getdata()
            getCategories()
        }
    }

    private suspend fun getCategories() {
        firebaseTestRepo.uiState.collect {
            when (it) {
                is ServerResponse.isLoading -> isLoading.postValue(true)
                is ServerResponse.onSuccess -> categories.postValue(it.data)
                is ServerResponse.onError -> isErrorExist.postValue(true)
                else -> {
                    Log.i("Error", it.data.toString())
                }
            }
        }
    }
}
