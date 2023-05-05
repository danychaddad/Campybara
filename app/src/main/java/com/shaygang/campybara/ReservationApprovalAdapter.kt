package com.shaygang.campybara

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.AppCompatImageButton
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class ReservationApprovalAdapter(val reservationIds: ArrayList<String>, val context : Context) : RecyclerView.Adapter<ReservationApprovalAdapter.ReservationApprovalViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup,viewType: Int): ReservationApprovalViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.reservation_request_item, parent, false)
        return ReservationApprovalViewHolder(view)
    }

    override fun getItemCount(): Int {
        return reservationIds.size
    }

    override fun onBindViewHolder(holder: ReservationApprovalViewHolder, position: Int) {
        val reservationId = reservationIds[position]
        var reservation : Reservation
        Reservation.getReservationById(reservationId) {
            reservation = it!!
            Group.getGroupFromId(reservation.requestingGroupId) {
                holder.groupName.text = it!!.name
                Glide.with(context).load(it!!.groupImageUrl).into(holder.groupImage)
            }
            holder.nbOfPeople.text = it.nbOfPeople.toString()
            holder.fromDate.text = "${it.reservationFromDate.date}/${it.reservationFromDate.month}/${it.reservationFromDate.year + 1900}"
            holder.toDate.text = "${it.reservationToDate.date}/${it.reservationToDate.month}/${it.reservationToDate.year + 1900}"
            holder.acceptBtn.setOnClickListener {
                Toast.makeText(holder.itemView.context,"Approved!", Toast.LENGTH_SHORT).show()
                reservation.approve()
                removeItem(position)
            }
            holder.rejectBtn.setOnClickListener {
                Toast.makeText(holder.itemView.context,"Declined!", Toast.LENGTH_SHORT).show()
                reservation.decline()
                removeItem(position)
            }
        }
    }

    fun removeItem(position: Int) {
        reservationIds.removeAt(position)
        notifyItemRemoved(position)
    }

    class ReservationApprovalViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val groupName: TextView = itemView.findViewById(R.id.requestGroupName)
        val groupImage : ImageView = itemView.findViewById(R.id.requestGroupImage)
        val nbOfPeople : TextView = itemView.findViewById(R.id.requestCapacityText)
        val fromDate : TextView = itemView.findViewById(R.id.requestFromDate)
        val toDate : TextView = itemView.findViewById(R.id.requestToDate)
        val acceptBtn : AppCompatImageButton = itemView.findViewById<AppCompatImageButton>(R.id.confirmReservation)
        val rejectBtn : AppCompatImageButton = itemView.findViewById<AppCompatImageButton>(R.id.rejectReservation)
    }
}
