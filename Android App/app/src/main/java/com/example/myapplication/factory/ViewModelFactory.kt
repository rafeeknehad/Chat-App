package com.example.myapplication.factory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.myapplication.viewmodels.*

@Suppress("UNCHECKED_CAST")
class ViewModelFactory : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(LoginViewModel::class.java)) {
            return LoginViewModel() as T
        }
        if (modelClass.isAssignableFrom(SigninViewModel::class.java)) {
            return SigninViewModel() as T
        }
        if (modelClass.isAssignableFrom(ProfileViewModel::class.java)) {
            return ProfileViewModel() as T
        }
        if (modelClass.isAssignableFrom(ChatsViewModel::class.java)) {
            return ChatsViewModel() as T
        }
        if (modelClass.isAssignableFrom(ContactViewModel::class.java)) {
            return ContactViewModel() as T
        }
        if (modelClass.isAssignableFrom(MessagesViewModel::class.java)) {
            return MessagesViewModel() as T
        }
        throw IllegalArgumentException("UnknownViewModel")
    }
}