package com.example.myapplication.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.datasource.models.UserModel
import com.example.myapplication.services.ServicesBuilder
import com.example.myapplication.services.ServicesInt
import com.example.myapplication.state.Result
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class LoginViewModel() : ViewModel() {
    private val _userDetail =
        MutableStateFlow<Result<UserModel>>(Result.Empty)
    val userDetail: MutableStateFlow<Result<UserModel>> = _userDetail

    fun findUserDetail(email: String, pass: String) = viewModelScope.launch {
        _userDetail.value = Result.Loading
        val destinationService = ServicesBuilder.buildService(ServicesInt::class.java)
        val response = destinationService.findUser(email, pass)
        if (response.isSuccessful) {
            _userDetail.value = Result.Success(response.body())
        } else {
            _userDetail.value = Result.Error(response.message().toString())
        }
    }
}