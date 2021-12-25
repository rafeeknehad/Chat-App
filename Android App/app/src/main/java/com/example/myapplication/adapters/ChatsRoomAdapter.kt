package com.example.myapplication.adapters

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.R
import com.example.myapplication.datasource.models.ChatModel
import com.example.myapplication.datasource.models.MessageModel
import com.example.myapplication.datasource.models.UserModel
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class ChatsRoomAdapter(
    val context: Context,
    private val chatContactInfoList: MutableList<UserModel>,
    private val chatRoomInfo: MutableList<ChatModel>
) : RecyclerView.Adapter<ChatsRoomAdapter.ChatsRoomViewHolder>() {
    lateinit var messageDetail: MessageModel
    lateinit var listener: OnItemClickListener

    inner class ChatsRoomViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView),
        View.OnClickListener {
        val profileImage: CircleImageView = itemView.findViewById(R.id.chatRoomUserImage)
        val profileUserName: TextView = itemView.findViewById(R.id.chatRoomUserName)
        val lastMessage: TextView = itemView.findViewById(R.id.chatRoomUserMessageTxt)

        init {
            itemView.setOnClickListener(this)
        }

        override fun onClick(p0: View?) {
            val position = adapterPosition
            if (position != RecyclerView.NO_POSITION) {
                listener.onItemClick(position)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatsRoomViewHolder {
        return ChatsRoomViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.list_item_chat_room, parent, false)
        )
    }

    @DelicateCoroutinesApi
    override fun onBindViewHolder(holder: ChatsRoomViewHolder, position: Int) {
        val user = chatContactInfoList[position]
        val chatRoom = chatRoomInfo[position]
        if (user.profileImage != "") {
            Picasso.with(context)
                .load(user.profileImage)
                .into(holder.profileImage)
        }
        holder.profileUserName.text = user.displayName
        Log.d("rafeek", "onBindViewHolder: ${chatRoom.id} ${chatRoom.messageId}")
        GlobalScope.launch(Dispatchers.IO) {
            getMessageDetail(chatRoom.messageId, chatRoom.id, holder)
        }
    }

    override fun getItemCount(): Int {
        return chatContactInfoList.size
    }

    private fun getMessageDetail(
        messageId: String,
        chatId: String,
        holder: ChatsRoomViewHolder
    ) {
        Log.d("rafeek", "getMessageDetail: ${Thread.currentThread()}")
        if (messageId != "") {
            val databaseReference = FirebaseDatabase.getInstance()
            databaseReference.getReference("Messages").child(chatId).child(messageId)
                .addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        messageDetail = snapshot.getValue(MessageModel::class.java)!!
                        messageDetail.id = snapshot.key!!
                        holder.lastMessage.text = messageDetail.message
                    }

                    override fun onCancelled(error: DatabaseError) {
                        TODO("Not yet implemented")
                    }
                })
        }
    }

    fun setOnClickListener(listener: OnItemClickListener) {
        this.listener = listener
    }

    interface OnItemClickListener {
        fun onItemClick(position: Int)
    }
}