package com.example.myapplication.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.datasource.models.ChatModel
import com.example.myapplication.datasource.models.UserModel
import com.example.myapplication.services.ServicesBuilder
import com.example.myapplication.services.ServicesInt
import com.example.myapplication.state.Result
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class ChatsViewModel : ViewModel() {
    private val _chatRoomDetail = MutableStateFlow<Result<List<ChatModel>>>(Result.Empty)
    private val _contactInfo = MutableStateFlow<Result<MutableList<UserModel>>>(Result.Empty)
    val contactInfo = _contactInfo
    val chatRoomDetail = _chatRoomDetail
    private val databaseReference = FirebaseDatabase.getInstance()

    fun getAllChatRoom(sender: String) = viewModelScope.launch {
        _chatRoomDetail.value = Result.Loading
        databaseReference.getReference("Chats")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val chatRoomList: MutableList<ChatModel> = ArrayList()
                    for (ds in snapshot.children) {
                        val chatRoom = ds.getValue(ChatModel::class.java)
                        if (chatRoom?.sender != sender && chatRoom?.receiver != sender) {
                            contactInfo
                        }
                        if (chatRoom?.messageId == "") {
                            continue
                        }
                        chatRoom?.id = ds.key!!
                        chatRoomList.add(chatRoom!!)
                    }
                    Log.d("rafeek", "onDataChange chatRoomList $chatRoomList ")
                    _chatRoomDetail.value = Result.Success(chatRoomList)
                }

                override fun onCancelled(error: DatabaseError) {
                    _chatRoomDetail.value = Result.Error(error.message)
                }
            })
    }

    fun getContactsInfo(listOfNumber: List<ChatModel>, number: String) = viewModelScope.launch {
        val contactsInfo: MutableList<UserModel> = ArrayList()
        _contactInfo.value = Result.Loading
        val server = ServicesBuilder.buildService(ServicesInt::class.java)
        var numberSearch: String = ""
        listOfNumber.forEach {
            numberSearch = if (it.receiver == number) {
                it.sender
            } else {
                it.receiver
            }
            val response = server.getContactInfo(numberSearch)
            if (response.isSuccessful) {
                Log.d("rafeek", "getContactsInfo: ${response.body()}")
                contactsInfo.add(response.body()!!)
            }
        }
        if (contactsInfo.size > 0) {
            _contactInfo.value = Result.Success(contactsInfo)
        } else {
            _contactInfo.value = Result.Error("Empty list")
        }
    }
}