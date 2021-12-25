package com.example.myapplication.ui.navigation2

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.MainActivity
import com.example.myapplication.R
import com.example.myapplication.adapters.MessagesAdapter
import com.example.myapplication.datasource.models.ContactModel
import com.example.myapplication.datasource.models.MessageModel
import com.example.myapplication.factory.ViewModelFactory
import com.example.myapplication.notification.FirebaseService
import com.example.myapplication.notification.NotificationData
import com.example.myapplication.notification.PushNotification
import com.example.myapplication.notification.RetrofitInstance
import com.example.myapplication.state.Result
import com.example.myapplication.viewmodels.MessagesViewModel
import com.google.firebase.messaging.FirebaseMessaging
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_main2.*
import kotlinx.android.synthetic.main.fragment_messages_body.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch


const val TOPIC = "/topics/myTopic"
class MessagesBodyFragment : Fragment() {
    private val args: MessagesBodyFragmentArgs by navArgs()
    private lateinit var senderNumber: String
    private lateinit var sharedReference: SharedPreferences
    private var contactDetail: ContactModel? = null
    private lateinit var chatId: String
    private lateinit var token: String
    private lateinit var chatFragmentViewModel: MessagesViewModel
    private val messageList: MutableList<MessageModel> = ArrayList()
    private lateinit var messageAdapter: MessagesAdapter
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_messages_body, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val viewModeFactory = ViewModelFactory()
        contactDetail = args.contactDetail
        chatFragmentViewModel =
            ViewModelProvider(this, viewModeFactory)[MessagesViewModel::class.java]
        getCurrentUserDetailFun()

        initNotification()
        if (contactDetail == null) {
            val chatRoomDetail = args.chatRoomDetail
            chatId = chatRoomDetail!!.id
            chatFragmentViewModel.updateMessageSeen(chatId, args.chatRoomDetail?.receiver!!)
            getAllChatMessages()
        } else {
            checkChatRoomFun()
        }
        initRecyclerViewFun()

        chatFragmentSend.setOnClickListener {
            if (chatFragmentEditTxt.text.trim().toString() != "") {
                sendMessageFun(chatFragmentEditTxt.text.trim().toString())
                chatFragmentEditTxt.setText("")
            } else {
                Toast.makeText(context, "can`t send empty message", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun initNotification() {
        FirebaseMessaging.getInstance().subscribeToTopic(TOPIC)
        getContactToken()
    }

    private fun getContactToken() {
        Log.d("rafeek", "getContactToken: $chatFragmentViewModel")
        if (contactDetail == null) {
            val number = if (args.chatRoomDetail?.receiver != senderNumber) {
                args.chatRoomDetail?.sender
            } else {
                args.chatRoomDetail?.receiver
            }
            chatFragmentViewModel.getTokenField(number!!)
        } else {
            chatFragmentViewModel.getTokenField(contactDetail!!.phoneNumber)
        }
        lifecycleScope.launchWhenResumed {
            chatFragmentViewModel.tokenField.collect {
                when (it) {
                    is Result.Success -> {
                        FirebaseService.token = it.data?.token
                        Log.d("rafeek", "getContactToken: ${it.data?.token}")
                        token = it.data?.token!!
                    }
                    is Result.Error -> {
                        Log.d("rafeek", "getContactToken: ${it.message}")
                    }
                    else -> Unit
                }
            }
        }
    }

    private fun initRecyclerViewFun() {
        messageAdapter = MessagesAdapter(requireContext(), messageList, senderNumber)
        chatFragmentRecyclerView.adapter = messageAdapter
        chatFragmentRecyclerView.layoutManager =
            LinearLayoutManager(context, RecyclerView.VERTICAL, false)
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun sendMessageFun(message: String) {
        Log.d("rafeek", "sendMessageFun: $message")
        val massage =
            MessageModel(senderNumber, message)
        messageList.add(massage)
        messageAdapter.notifyDataSetChanged()
        saveMessageFireBaseFun(massage)

        PushNotification(NotificationData("rafeek", message), FirebaseService.token!!).also {
            sendNotification(it)
        }
    }

    private fun checkChatRoomFun() {
        val receiverNumber = contactDetail?.phoneNumber
        chatFragmentViewModel.findChatIdFun(senderNumber, receiverNumber!!)
        lifecycleScope.launch {
            chatFragmentViewModel.chatDetail.collect {
                when (it) {
                    is Result.Success -> {
                        Log.d(
                            "rafeek",
                            "checkChatRoomFun: ${it.data?.id} ${it.data?.sender} ${it.data?.receiver}"
                        )
                        chatId = it.data?.id!!
                        chatFragmentViewModel.updateMessageSeen(
                            chatId,
                            contactDetail?.phoneNumber!!
                        )
                        getAllChatMessages()
                    }
                    else -> Unit
                }
            }
        }
    }

    private fun getCurrentUserDetailFun() {
        sharedReference =
            context?.getSharedPreferences(MainActivity.SHARD_REFERENCE, Context.MODE_PRIVATE)!!
        senderNumber = sharedReference.getString(MainActivity.NUMBER, "")!!
        Log.d("rafeek", "getCurrentUserDetailFun: $senderNumber")
    }

    private fun saveMessageFireBaseFun(message: MessageModel) {
        chatFragmentViewModel.addNewMessage(message, chatId)
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun getAllChatMessages() {
        Log.d("rafeek", "getAllChatMessages: $chatId")
        chatFragmentViewModel.getAllMessage(chatId)
        lifecycleScope.launchWhenCreated {
            chatFragmentViewModel.messageList.collect {
                when (it) {
                    is Result.Success -> {
                        Log.d("rafeek", "getAllChatMessages: ${it.data?.size}")
                        messageList.clear()
                        messageList.addAll(it.data!!)
                        messageAdapter.notifyDataSetChanged()
                    }
                    else -> Unit
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        activity?.mainActivityBtn?.visibility = View.GONE
    }

    private fun sendNotification(notification: PushNotification) =
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = RetrofitInstance.api.postNotification(notification)
                if (response.isSuccessful) {
                    Log.d("rafeek", "sendNotification: ${Gson().toJson(response)}")
                } else {
                    Log.d("rafeek", "sendNotification: ${response.errorBody().toString()}")
                }
            } catch (ex: Exception) {
                Log.d("rafeek", "sendNotification: ${ex.message}")
            }
        }
}