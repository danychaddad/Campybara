package com.shaygang.campybara

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class ApproveReservationsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_approve_reservations)
        val recyclerView = findViewById<RecyclerView>(R.id.reservationRequestList)
        recyclerView.layoutManager = LinearLayoutManager(this)
        val campsiteId = intent.extras!!.getString("campsiteId")
        Log.d("campId",campsiteId!!)
        Reservation.getCampsiteReservations(campsiteId!!) {
            println(it.toString())
            recyclerView.adapter = ReservationApprovalAdapter(it!!,this)
        }
    }
}
