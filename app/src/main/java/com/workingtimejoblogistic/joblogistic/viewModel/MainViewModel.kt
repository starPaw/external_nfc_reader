package com.workingtimejoblogistic.joblogistic.viewModel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.workingtimejoblogistic.joblogistic.api.RetrofitInstance
import com.workingtimejoblogistic.joblogistic.model.Worker
import kotlinx.coroutines.launch
import retrofit2.Response

class Rpository() {
    suspend fun getWorker(): Array<Worker> {
        return RetrofitInstance.api.getWorker()
    }

    suspend fun getWorkerByCard(card: Int): Array<Worker> {
        return RetrofitInstance.api.getWorkerByCard(card)
    }

    suspend fun postTime(base_64_photo: String, card: String): Response<Worker> {
        return RetrofitInstance.api.postTime(base_64_photo, card)
    }
}

class MainViewModel(private val repository: Rpository) : ViewModel() {
    val myResponse: MutableLiveData<Array<Worker>> = MutableLiveData()
    private val myPostResponse: MutableLiveData<Response<Worker>> = MutableLiveData()
    fun getWorker() {
        viewModelScope.launch {
            val response = repository.getWorker()
            myResponse.value = response
        }
    }

    fun getWorkerByCard(card: Int) {
        viewModelScope.launch {
            val response = repository.getWorkerByCard(card)
            myResponse.value = response
        }
    }

    fun postTime(base_64_photo: String, card: String) {
        viewModelScope.launch {
            val response = repository.postTime(base_64_photo, card)
            myPostResponse.value = response
        }
    }

}