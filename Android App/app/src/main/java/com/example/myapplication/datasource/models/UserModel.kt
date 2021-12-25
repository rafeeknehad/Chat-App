package com.example.myapplication.datasource.models

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize
import java.util.*

@Parcelize
data class UserModel(
    @SerializedName("DisplayName")
    var displayName: String = "",
    @SerializedName("Email")
    var email: String = "",
    @SerializedName("Password")
    var password: String = "",
    @SerializedName("Number")
    var number: String = "",
    @SerializedName("ProfileImage")
    var profileImage: String = "",
    @SerializedName("createdAt")
    var timestamp: Date? = null,
    @SerializedName("_id")
    var id: String? = null,
    @SerializedName("Token")
    var token: String = ""

) : Parcelable