package com.example.kathavichar.viewModel

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.kathavichar.model.Category
import com.example.kathavichar.repositories.FirebaseTestRepo
import org.koin.java.KoinJavaComponent.inject

class MainViewModel : ViewModel() {

    private val firebaseTestRepo: FirebaseTestRepo by inject(FirebaseTestRepo::class.java)
    var _categories = MutableLiveData<List<Category>>()

    suspend fun GetCategories() {
        _categories.postValue(firebaseTestRepo.getdata())
    }
}
