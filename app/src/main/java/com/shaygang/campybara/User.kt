package com.shaygang.campybara

import android.util.Log
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.tasks.await

class User(val uid: String, val firstName: String, val lastName: String, val phoneNb: String, val dateOfBirth: String,
           val email: String, val profileImageUrl: String) {
    companion object {
        fun loadUserFromUid(uid: String, callback: (User?) -> Unit) {
            val database = Firebase.database
            val usersRef = database.getReference("users")
            val userRef = usersRef.child(uid)
            userRef.get().addOnSuccessListener { snapshot ->
                val user = snapshot.getValue(User::class.java)
                callback(user)
            }.addOnFailureListener { exception ->
                callback(null)
            }
        }
    }
    constructor() : this("","","","","","","")
}