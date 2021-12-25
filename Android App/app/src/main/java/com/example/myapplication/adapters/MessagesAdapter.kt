package com.example.myapplication.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.R
import com.example.myapplication.datasource.models.MessageModel
import java.time.LocalDateTime

class MessagesAdapter(
    val context: Context,
    private val messageList: MutableList<MessageModel>,
    private val sender: String
) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    inner class SenderMessageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val message: TextView = itemView.findViewById(R.id.itemSenderMessage)
        val time: TextView = itemView.findViewById(R.id.itemSenderTime)
        val seenImg: ImageView = itemView.findViewById(R.id.itemSenderSeen)
    }

    inner class ReceiverMessageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val message: TextView = itemView.findViewById(R.id.itemReceiverMessage)
        val time: TextView = itemView.findViewById(R.id.itemReceiverTime)
        val seenImg: ImageView = itemView.findViewById(R.id.itemReceiverSeen)
    }

    override fun getItemViewType(position: Int): Int {
        return if (messageList[position].sender == sender) {
            0
        } else {
            1
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == 0) {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.list_item_sender_message, parent, false)
            SenderMessageViewHolder(view)
        } else {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.list_item_receiver_message, parent, false)
            ReceiverMessageViewHolder(view)
        }
    }

    @SuppressLint("SetTextI18n")
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val message = messageList[position]
        if (this.getItemViewType(position) == 0) {
            val senderHolder = holder as SenderMessageViewHolder
            senderHolder.message.text = message.message
            val timeDate = LocalDateTime.parse(message.time)
            senderHolder.time.text = "${timeDate.hour}:${timeDate.minute}"
            if (!message.seen) {
                senderHolder.seenImg.setBackgroundResource(R.drawable.not_seen_sender_icon)
            } else {
                senderHolder.seenImg.setBackgroundResource(R.drawable.seen_icon)
            }
        } else {
            val receiverHolder = holder as ReceiverMessageViewHolder
            receiverHolder.message.text = message.message
            val timeDate = LocalDateTime.parse(message.time)
            receiverHolder.time.text = "${timeDate.hour}:${timeDate.minute}"
            if (!message.seen) {
                receiverHolder.seenImg.setBackgroundResource(R.drawable.not_seen_receiver_icon)
            } else {
                receiverHolder.seenImg.setBackgroundResource(R.drawable.seen_icon)
            }
        }
    }

    override fun getItemCount(): Int {
        return messageList.size
    }

    /*@SuppressLint("NotifyDataSetChanged")
    fun notifyDataChangeFun(newMessageList: MutableList<MessageModel>) {
        Log.d("rafeek", "notifyDataChangeFun: ${newMessageList.size}")
        this.messageList.clear()
        this.messageList.addAll(newMessageList)
        Log.d("rafeek", "notifyDataChangeFun: ${this.messageList.size}")
        notifyDataSetChanged()
    }*/
}