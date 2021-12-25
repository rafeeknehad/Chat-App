package com.example.myapplication.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.datasource.models.ChatModel
import com.example.myapplication.datasource.models.MessageModel
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

class MessagesViewModel : ViewModel() {
    private val databaseReference = FirebaseDatabase.getInstance()
    private val _chatDetail = MutableStateFlow<Result<ChatModel>>(Result.Empty)
    private val _messageList = MutableStateFlow<Result<MutableList<MessageModel>>>(Result.Empty)
    private val _tokenField = MutableStateFlow<Result<UserModel>>(Result.Empty)
    val messageList = _messageList
    val chatDetail = _chatDetail
    val tokenField = _tokenField
    fun findChatIdFun(senderNumber: String, receiverNumber: String) = viewModelScope.launch {
        Log.d("rafeek", "findChatIdFun: $senderNumber $receiverNumber")
        databaseReference.getReference("Chats").orderByChild("sender").equalTo(senderNumber)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    for (ds in snapshot.children) {
                        val chatBody = ds.getValue(ChatModel::class.java)
                        Log.d("rafeek", "onDataChange: ${chatBody?.sender} ${chatBody?.receiver}")
                        if (chatBody?.receiver == receiverNumber) {
                            chatBody.id = ds.key!!
                            _chatDetail.value = Result.Success(chatBody)
                            return
                        }
                    }
                    findChatIdReverseFun(senderNumber, receiverNumber)
                }

                override fun onCancelled(error: DatabaseError) {
                    _chatDetail.value = Result.Error(error.message)
                }
            })
    }

    private fun findChatIdReverseFun(senderNumber: String, receiverNumber: String) =
        viewModelScope.launch {
            databaseReference.getReference("Chats").orderByChild("receiver").equalTo(senderNumber)
                .addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        for (ds in snapshot.children) {
                            val chatBody = ds.getValue(ChatModel::class.java)
                            if (chatBody?.receiver == senderNumber) {
                                chatBody.id = ds.key!!
                                _chatDetail.value = Result.Success(chatBody)
                                return
                            }
                        }
                        val chatBody = ChatModel(senderNumber, receiverNumber)
                        createChatIdFun(chatBody)
                        Log.d("rafeek", "o    nDataChange: e7na hena")
                    }

                    override fun onCancelled(error: DatabaseError) {
                    }

                })
        }

    fun createChatIdFun(chatBody: ChatModel) = viewModelScope.launch {
        val key = databaseReference.getReference("Chats").push().key
        databaseReference.getReference("Chats").child(key!!).setValue(chatBody)
        chatBody.id = key
        _chatDetail.value = Result.Success(chatBody)
    }

    fun addNewMessage(messageBody: MessageModel, chatId: String) = viewModelScope.launch {
        val key = databaseReference.getReference("Messages").child(chatId).push().key
        databaseReference.getReference("Messages").child(chatId).child(key!!).setValue(messageBody)
        messageBody.id = key
        updateChatIdFun(messageBody, chatId)
    }

    private fun updateChatIdFun(messageBody: MessageModel, chatId: String) {
        databaseReference.getReference("Chats").child(chatId).child("lastMessage")
            .setValue(messageBody.time)
        databaseReference.getReference("Chats").child(chatId).child("messageId")
            .setValue(messageBody.id)
    }

    fun updateMessageSeen(chatId: String, receiverNumber: String) {
        databaseReference.getReference("Messages").child(chatId).orderByChild("sender")
            .equalTo(receiverNumber)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    for (ds in snapshot.children) {
                        val message = ds.getValue(MessageModel::class.java)
                        if (message?.seen == false) {
                            Log.d("rafeek", "onDataChange: e7na hena ${message.seen}")
                            ds.ref.child("seen").setValue(true)
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.d("rafeek", "onCancelled: ${error.message}")
                }
            })
    }

    fun getAllMessage(charId: String) = viewModelScope.launch {
        _messageList.value = Result.Loading
        databaseReference.getReference("Messages").child(charId)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val list: MutableList<MessageModel> = ArrayList()
                    for (ds in snapshot.children) {
                        val message = ds.getValue(MessageModel::class.java)
                        message?.id = ds.key!!
                        list.add(message!!)
                    }
                    _messageList.value = Result.Success(list)
                }

                override fun onCancelled(error: DatabaseError) {
                    _messageList.value = Result.Error(error.message)
                }

            })
    }

    fun getTokenField(number: String) = viewModelScope.launch {
        val response = ServicesBuilder.buildService(ServicesInt::class.java)
            .getTokenField(number)
        if (response.isSuccessful) {
            _tokenField.value = Result.Success(response.body())
        } else {
            _tokenField.value = Result.Error(response.message())
        }
    }
}