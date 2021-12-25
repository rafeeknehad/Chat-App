package com.example.myapplication.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.datasource.models.UserModel
import com.example.myapplication.services.ServicesBuilder
import com.example.myapplication.services.ServicesInt
import com.example.myapplication.state.Result
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class SigninViewModel() : ViewModel() {
    private val _createNewUser = MutableStateFlow<Result<UserModel>>(Result.Empty)
    val createUserState: MutableStateFlow<Result<UserModel>> = _createNewUser

    fun createNewUser(user: UserModel) = viewModelScope.launch {
        _createNewUser.value = Result.Loading
        val destinationService = ServicesBuilder.buildService(ServicesInt::class.java)
        val response = destinationService.createNewUser(user)
        Log.d("rafeek", "createNewUser: New User")
        if (response.isSuccessful) {
            _createNewUser.value = Result.Success(response.body())
        } else {
            _createNewUser.value = Result.Error(response.message())
        }
    }
}