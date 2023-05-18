package com.workingtimejoblogistic.joblogistic.api

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import retrofit2.Response
import retrofit2.Retrofit

class Rpository() {
    suspend fun getWorker(): Array<Worker> {
        return RetrofitInstance.api.getWorker()
    }

    suspend fun postTime(base_64_photo: String, card: String): Response<Worker> {
        return RetrofitInstance.api.postTime(base_64_photo, card)
    }
}

class MainViewModel(private val repository: Rpository) : ViewModel() {
    val myResponse: MutableLiveData<Array<Worker>> = MutableLiveData()
    val myPostResponse: MutableLiveData<Response<Worker>> = MutableLiveData()
    fun getWorker() {
        viewModelScope.launch {
            val response = repository.getWorker()
            myResponse.value = response
        }
    }

    fun post(base_64_photo: String, card: String) {
        viewModelScope.launch {
            val response = repository.postTime(base_64_photo, card)
            myPostResponse.value = response
        }
    }
}