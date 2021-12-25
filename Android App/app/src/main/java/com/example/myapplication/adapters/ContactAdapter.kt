package com.example.myapplication.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.net.toUri
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.R
import com.example.myapplication.datasource.models.ContactModel
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.android.synthetic.main.list_item_contact.view.*

class ContactAdapter(val context: Context, private val contactList: ArrayList<ContactModel>) :
    RecyclerView.Adapter<ContactAdapter.ContactViewHolder>() {

    lateinit var listener: OnItemClickListener

    inner class ContactViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView),
        View.OnClickListener {
        val imageView: CircleImageView = itemView.ContactItemImageView
        val userName: TextView = itemView.ContactItemUserName
        val userNumber: TextView = itemView.ContactItemNumber

        init {
            itemView.setOnClickListener(this)
        }

        override fun onClick(p0: View?) {
            val position = adapterPosition
            if (position != RecyclerView.NO_POSITION) {
                listener.onClick(position)
            }
        }
    }

    interface OnItemClickListener {
        fun onClick(position: Int)
    }

    fun setOnItemClickListener(listener: OnItemClickListener) {
        this.listener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContactViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.list_item_contact, parent, false)
        return ContactViewHolder(view)
    }

    override fun onBindViewHolder(holder: ContactViewHolder, position: Int) {
        val contactUser = contactList[position]
        if (contactUser.image != null) {
            Picasso.with(context).load(contactUser.image.toUri()).into(holder.imageView)
        }
        holder.userName.text = contactUser.name
        holder.userNumber.text = contactUser.phoneNumber
    }

    override fun getItemCount(): Int {
        return contactList.size
    }
}