package com.shaygang.campybara.Class

import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class Campsite(val name: String, val description: String? = "No description provided", val capacity: Int? = -1, val imageUrl: String?, val rating: Double? = 0.0, val ownerUID: String, val location: ArrayList<Double>) {
    constructor() : this("","",1,"",1.0,"", arrayListOf())
    companion object {
        private var firebaseDatabase = FirebaseDatabase.getInstance()
        fun getCampsiteIds(campsiteIdList : ArrayList<String>, callback: (ArrayList<String>?) -> Unit) {
            campsiteIdList.clear()
            var databaseRef = firebaseDatabase.getReference("campsites")
            databaseRef.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    for (child in snapshot.children) {
                        campsiteIdList.add(child.key!!)
                    }
                    callback(campsiteIdList)
                }
                override fun onCancelled(error: DatabaseError) {
                }
            })
        }

        fun getCampsiteFromId(campsiteId : String, callback: (Campsite?) -> Unit) {
            var databaseRef = firebaseDatabase.getReference("campsites/$campsiteId")
            databaseRef.get().addOnSuccessListener {
                callback(it.getValue(Campsite::class.java))
            }
        }
    }
}