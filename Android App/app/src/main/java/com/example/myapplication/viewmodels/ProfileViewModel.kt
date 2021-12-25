package com.example.myapplication.viewmodels

import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.datasource.models.UserModel
import com.example.myapplication.services.ServicesBuilder
import com.example.myapplication.services.ServicesInt
import com.example.myapplication.state.Result
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class ProfileViewModel() : ViewModel() {
    private val _profileImage = MutableStateFlow<Result<Uri>>(Result.Empty)
    private val _updatedUser = MutableStateFlow<Result<UserModel>>(Result.Empty)
    val profileImage = _profileImage
    val updateUser = _updatedUser
    private val storageReference = FirebaseStorage.getInstance().getReference("ProfileImages")

    fun getProfileImage(number: String) = viewModelScope.launch(Dispatchers.IO) {
        _profileImage.value = Result.Loading
        storageReference.child(number).downloadUrl.addOnSuccessListener {
            Log.d("rafeek", "getProfileImage: $it")
            _profileImage.value = Result.Success(it)
        }
            .addOnFailureListener {
                _profileImage.value = Result.Error(it.message!!)
            }
    }

    fun updateUserDetail(user: UserModel) = viewModelScope.launch(Dispatchers.IO) {
        _updatedUser.value = Result.Loading
        val server = ServicesBuilder.buildService(ServicesInt::class.java)
        val response = server.updateUserDetails(user.id!!, user)
        if (response.isSuccessful) {
            _updatedUser.value = Result.Success(response.body())
        } else {
            _updatedUser.value = Result.Error(response.message())
        }
    }
}