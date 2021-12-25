package com.example.myapplication.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.datasource.models.UserModel
import com.example.myapplication.services.ServicesBuilder
import com.example.myapplication.services.ServicesInt
import com.example.myapplication.state.Result
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class ContactViewModel : ViewModel() {

    private val _contactDetail = MutableStateFlow<Result<UserModel>>(Result.Empty)
    val contactDetail = _contactDetail

    fun getContactInfo(number: String) = viewModelScope.launch {
        val server = ServicesBuilder.buildService(ServicesInt::class.java)
        val response = server.getContactInfo(number)
        if (response.isSuccessful) {
            _contactDetail.emit(Result.Success(response.body()))
        } else {
            _contactDetail.emit(Result.Error(number))
        }
    }
}