package com.shaygang.campybara.Adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.shaygang.campybara.Class.Campsite
import com.shaygang.campybara.R
import com.shaygang.campybara.Reservation

open class ReservationAdapter(private val reservationIds : ArrayList<String>, val context : Context) : RecyclerView.Adapter<ReservationAdapter.ReservationViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReservationViewHolder {
        // Inflate your view holder layout here
        val view = LayoutInflater.from(parent.context).inflate(R.layout.reservation_item, parent, false)
        return ReservationViewHolder(view)
    }

    override fun getItemCount(): Int {
        return reservationIds.size
    }

    override fun onBindViewHolder(holder: ReservationViewHolder, position: Int) {
        val reservationId = reservationIds[position]
        var reservation : Reservation
        Reservation.getReservationById(reservationId) {
            reservation = it!!
            Campsite.getCampsiteFromId(reservation.campsiteId) {
                holder.campsiteName.text = it!!.name
                Glide.with(context).load(it.imageUrl).into(holder.campsiteImage)
            }
            holder.nbOfPeople.text = it.nbOfPeople.toString()
            holder.fromDate.text =
                "${it.reservationFromDate.date}/${it.reservationFromDate.month}/${it.reservationFromDate.year + 1900}"
            holder.toDate.text =
                "${it.reservationToDate.date}/${it.reservationToDate.month}/${it.reservationToDate.year + 1900}"
        }
    }

    open class ReservationViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var campsiteName: TextView = itemView.findViewById(R.id.requestGroupName)
        val campsiteImage : ImageView = itemView.findViewById(R.id.requestGroupImage)
        val nbOfPeople : TextView = itemView.findViewById(R.id.requestCapacityText)
        val fromDate : TextView = itemView.findViewById(R.id.requestFromDate)
        val toDate : TextView = itemView.findViewById(R.id.requestToDate)
    }
}