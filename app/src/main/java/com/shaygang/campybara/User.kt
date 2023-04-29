package com.shaygang.campybara

import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import android.os.Parcelable

class User(val uid: String, val firstName: String, val lastName: String, val phoneNb: String, val dateOfBirth: String,
           val email: String, val profileImageUrl: String) {
    constructor() : this("","","","","","","") {
    }
    companion object {
        fun loadUserFromUid(uid: String, callback: (User?) -> Unit) {
            val database = Firebase.database
            val usersRef = database.getReference("users")
            val userRef = usersRef.child(uid)
            userRef.get().addOnSuccessListener { snapshot ->
                val user = snapshot.getValue(User::class.java)
                callback(user)
            }.addOnFailureListener {
                callback(null)
            }
        }

        fun getNameFromId(userId: String, callback: (String?) -> Unit) {
            var username : String = ""
            loadUserFromUid(userId) {user ->
                username = "${user!!.firstName.toString()} ${user.lastName.toString()}"
                callback(username)
            }
        }
    }
}