package com.shaygang.campybara

import com.google.firebase.database.FirebaseDatabase

class Group (val name: String, val desc: String, val creator:String, val groupImageUrl: String, val memberList : MutableList<String>) {
    constructor() : this("","","","", mutableListOf())
    companion object {
        fun getGroupFromId(groupId : String, callback: (Group?) -> Unit) {
            val firebaseDatabase = FirebaseDatabase.getInstance()
            val groupRef = firebaseDatabase.getReference("/groups/$groupId")
            groupRef.get().addOnSuccessListener {
                val group = it.getValue(Group::class.java)
                callback(group)
            }
        }
    }
}