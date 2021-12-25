package com.example.myapplication.datasource.models

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import com.google.firebase.database.Exclude
import java.time.LocalDateTime

@RequiresApi(Build.VERSION_CODES.O)
class MessageModel(
    val sender: String = "",
    val message: String = "",
    val seen: Boolean = false,
    val time: String? = LocalDateTime.now().toString()
) {
    @get:Exclude
    var id: String = ""

    constructor() : this("", "", false, "")

    init {
        Log.d("rafeek", "init $time")
        Log.d("rafeek", "init ${time.toString()}")
    }
}