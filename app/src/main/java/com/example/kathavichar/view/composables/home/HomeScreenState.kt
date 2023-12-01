package com.example.kathavichar.view.composables.home

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.navigation.NavHostController
import com.example.kathavichar.common.utils.isDataLoading
import com.example.kathavichar.network.ServerResponse
import com.example.kathavichar.viewModel.MainViewModel

@Composable
fun HomeScreenState(navigationController: NavHostController, viewModel: MainViewModel) {
    LaunchedEffect(Unit) {
        viewModel.getCategories()
    }
    val uiState by viewModel.uiState.collectAsState()

    when (uiState) {
        is ServerResponse.isLoading -> isDataLoading()
        is ServerResponse.onSuccess -> HomeCategories(uiState.data, navigationController)
        is ServerResponse.onError -> {}
    }
}
