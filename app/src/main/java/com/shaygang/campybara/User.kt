package com.shaygang.campybara

import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import android.os.Parcelable
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.getValue

class User(val uid: String, val firstName: String, val lastName: String, val phoneNb: String, val dateOfBirth: String,
           val email: String, val profileImageUrl: String, val isAdmin: Boolean, val isOwner: Boolean) {
    constructor() : this("","","","","","","", false, false) {
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

        fun getCurrentlyLoggedInUser() : String {
            return FirebaseAuth.getInstance().currentUser!!.uid
        }

        fun getGroupsWhereUserIsLeader(userId : String, callback: (ArrayList<String>) -> Unit) {
            val database = Firebase.database
            val groupIdList = arrayListOf<String>()
            val groupRef = database.getReference("groups")
            groupRef.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    for (child in snapshot.children) {
                        val group = child.getValue(Group::class.java)!!
                        if (group.creator == userId) {
                            groupIdList.add(child.key!!)
                        }
                    }
                    callback(groupIdList)
                }

                override fun onCancelled(error: DatabaseError) {
                }

            })
        }
    }
}