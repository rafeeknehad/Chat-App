package com.example.myapplication.datasource.models

import android.os.Parcelable
import com.google.firebase.database.Exclude
import kotlinx.android.parcel.Parcelize


@Parcelize
class ChatModel(
    val sender: String,
    val receiver: String,
    val lastMessage: String = "",
    val messageId: String = ""
) : Parcelable {
    @get:Exclude
    var id: String = ""

    constructor() : this("", "")
}
