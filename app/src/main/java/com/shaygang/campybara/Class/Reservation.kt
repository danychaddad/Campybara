package com.shaygang.campybara

import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.util.*

class Reservation (val campsiteId : String, val campsiteOwnerId : String,
                   val requestingUserId : String, val requestingGroupId : String,
                   val reservationFromDate : Date, val reservationToDate : Date,
                   val nbOfPeople : Int, val reservationState : ReservationState) {
    private val firebaseDatabase = FirebaseDatabase.getInstance()
    private lateinit var reservationId : String
    fun approve() {
        val ref = firebaseDatabase.getReference("reservations/$reservationId/reservationState")
        ref.setValue(ReservationState.ACCEPTED)
    }

    fun decline() {
        val ref = firebaseDatabase.getReference("reservations/$reservationId/reservationState")
        ref.setValue(ReservationState.REJECTED)
    }

    constructor() : this("", "", "", "", Date(), Date(), 1, ReservationState.ACCEPTED)

    companion object {
        fun getReservationById(reservationId: String, callback: (Reservation?) -> Unit) {
            val firebaseDatabase = FirebaseDatabase.getInstance()
            val ref = firebaseDatabase.getReference("reservations/$reservationId")
            ref.get().addOnSuccessListener {
                val reservation = it.getValue(Reservation::class.java)
                reservation!!.reservationId = it.key.toString()
                callback(reservation)
            }
        }

        fun getReservationsForGroup(groupId : String, callback: (ArrayList<String>?) -> Unit) {
            val firebaseDatabase = FirebaseDatabase.getInstance()
            val ref = firebaseDatabase.getReference("reservations")
            ref.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val reservations = arrayListOf<String>()
                    for (child in snapshot.children) {
                        print(child.child("requestingGroupId"))
                        if (child.child("requestingGroupId").value!! == groupId) {
                            reservations.add(child.key!!)
                        }
                    }
                    callback(reservations)
                }

                override fun onCancelled(error: DatabaseError) {
                }

            })
        }
        fun getGroupReservationForState(groupId : String, state : ReservationState, callback: (ArrayList<String>?) -> Unit) {
            getReservationsForGroup(groupId) { arrayList ->
                val reservationsForState = arrayListOf<String>()
                for (id in arrayList!!) {
                    getReservationById(id) {
                        var res = it!!
                        if (res.reservationState == state) {
                            reservationsForState.add(id)
                        }
                    }
                }
                callback(reservationsForState)
            }
        }

        fun getCampsiteReservations(campsiteId : String, callback: (ArrayList<String>?) -> Unit) {
            val firebaseDatabase = FirebaseDatabase.getInstance()
            val ref = firebaseDatabase.getReference("reservations")
            ref.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val reservations = arrayListOf<String>()
                    for (child in snapshot.children) {
//                        println(child)
                        if (child.child("campsiteId").value!! == campsiteId && child.child("reservationState").value!! == "PENDING") {
                            print(child.key)
                            reservations.add(child.key!!)
                        }
                    }
                    callback(reservations)
                }

                override fun onCancelled(error: DatabaseError) {
                }

            })
        }
    }
}

enum class ReservationState(val value: Int) {
    PENDING(0),
    ACCEPTED(1),
    REJECTED(2)
}
