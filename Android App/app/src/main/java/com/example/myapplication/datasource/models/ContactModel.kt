package com.example.myapplication.datasource.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
class ContactModel(val name: String, val phoneNumber: String, val image: String? = null) :
    Parcelable {
}