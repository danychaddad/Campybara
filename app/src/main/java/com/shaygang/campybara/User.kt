package com.shaygang.campybara

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
class User(val uid: String, val firstName: String, val lastName: String, val phoneNb: String, val dateOfBirth: String,
           val email: String, val profileImageUrl: String) : Parcelable {
    constructor() : this("","","","","","","") {
    }

}