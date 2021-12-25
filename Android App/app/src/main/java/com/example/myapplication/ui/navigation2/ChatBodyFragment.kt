package com.example.myapplication.ui.navigation2

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.MainActivity
import com.example.myapplication.R
import com.example.myapplication.adapters.ChatsRoomAdapter
import com.example.myapplication.datasource.models.ChatModel
import com.example.myapplication.datasource.models.UserModel
import com.example.myapplication.factory.ViewModelFactory
import com.example.myapplication.state.Result
import com.example.myapplication.viewmodels.ChatsViewModel
import kotlinx.android.synthetic.main.activity_main2.*
import kotlinx.android.synthetic.main.fragment_chat_body.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.withContext


class ChatBodyFragment : Fragment() {
    private lateinit var currentUser: UserModel
    private lateinit var viewModel: ChatsViewModel
    private val contactInfoRoom: MutableList<UserModel> = ArrayList()
    private val chatsRoomList: MutableList<ChatModel> = ArrayList()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_chat_body, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val viewModeFactory = ViewModelFactory()
        viewModel = ViewModelProvider(this, viewModeFactory)[ChatsViewModel::class.java]
        getCurrentUserDetail()
        getAllChatRoom()
        chatsFragmentFab.setOnClickListener {
            val navController = findNavController()
            navController.navigate(R.id.action_chatBodyFragment_to_contactFragment)
        }
    }

    override fun onResume() {
        super.onResume()
        activity?.mainActivityBtn?.visibility = View.VISIBLE
    }

    private fun getAllChatRoom() {
        viewModel.getAllChatRoom(currentUser.number)
        lifecycleScope.launchWhenResumed {
            viewModel.chatRoomDetail.collect {
                when (it) {
                    is Result.Success -> {
                        chatsRoomList.clear()
                        val list = it.data
                        val sortedList = list?.sortedBy { userMode -> userMode.lastMessage }
                        chatsRoomList.addAll(sortedList!!)
                        getContactInfoFun(sortedList)
                    }
                    is Result.Loading -> {
                        chatsProgressBar.visibility = View.VISIBLE
                    }
                    else -> Unit
                }
            }
        }
    }

    private fun getContactInfoFun(sortedList: List<ChatModel>?) {
        lifecycleScope.launchWhenResumed {
            viewModel.getContactsInfo(sortedList!!, currentUser.number)
            viewModel.contactInfo.collect {
                when (it) {
                    is Result.Success -> {
                        withContext(Dispatchers.Main) {
                            chatsProgressBar.visibility = View.GONE
                        }
                        contactInfoRoom.clear()
                        contactInfoRoom.addAll(it.data!!)
                        initAdapter()
                    }
                    is Result.Error -> {
                        withContext(Dispatchers.Main) {
                            chatsProgressBar.visibility = View.GONE
                        }
                    }
                    else -> Unit
                }
            }
        }
    }

    private fun getCurrentUserDetail() {
        val sp = context?.getSharedPreferences(MainActivity.SHARD_REFERENCE, Context.MODE_PRIVATE)
        currentUser = UserModel(
            sp?.getString(MainActivity.DISPLAY_NAME, "")!!,
            sp.getString(MainActivity.EMAIL, "")!!,
            sp.getString(MainActivity.PASSWORD, "")!!,
            sp.getString(MainActivity.NUMBER, "")!!,
            sp.getString(MainActivity.PROFILE_IMAGE, "")!!,
            id = sp.getString(MainActivity.ID, ""),
        )
    }

    private fun initAdapter() {
        Log.d("rafeek ssss", "initAdapter: ${contactInfoRoom.size} ${chatsRoomList.size} ")
        val adapter = ChatsRoomAdapter(requireContext(), contactInfoRoom, chatsRoomList)
        chatsFragmentRecyclerView.adapter = adapter
        chatsFragmentRecyclerView.layoutManager =
            LinearLayoutManager(context, RecyclerView.VERTICAL, false)

        adapter.setOnClickListener(object : ChatsRoomAdapter.OnItemClickListener {
            override fun onItemClick(position: Int) {
                Log.d(
                    "rafeek",
                    "onItemClick: ${contactInfoRoom[position].displayName} ${chatsRoomList[position].id}"
                )
                val action =
                    ChatBodyFragmentDirections.actionChatBodyFragmentToMessagesBodyFragment(
                        contactInfoRoom[position].displayName,
                        null,
                        chatsRoomList[position],
                        contactInfoRoom[position]
                    )
                findNavController().navigate(action)
            }

        })
    }
}