package com.workingtimejoblogistic.joblogistic.viewModelFactory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.workingtimejoblogistic.joblogistic.viewModel.MainViewModel
import com.workingtimejoblogistic.joblogistic.viewModel.Rpository

class MainViewModelFactory(private val repository: Rpository): ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return MainViewModel(repository) as T
    }
}