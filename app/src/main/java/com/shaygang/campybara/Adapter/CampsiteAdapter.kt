package com.shaygang.campybara

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.gms.location.LocationServices
import com.shaygang.campybara.Activity.CampsiteDetailsActivity
import com.shaygang.campybara.Activity.ReviewHelper
import com.shaygang.campybara.Class.Campsite
import kotlin.math.*

class CampsiteAdapter(private val campsiteIdList : ArrayList<String>, val context: Context) : RecyclerView.Adapter<CampsiteAdapter.CampsiteViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CampsiteViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.list_item, parent,false)
        return CampsiteViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return campsiteIdList.size
    }

    override fun onBindViewHolder(holder: CampsiteViewHolder, position: Int) {
        var currentItemId = campsiteIdList[position]
        Campsite.getCampsiteFromId(currentItemId) {
            val currentItem = it!!
            val fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(context)
            if (ActivityCompat.checkSelfPermission(
                    context,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                    context,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
            ) {
            }
            fusedLocationProviderClient.lastLocation
                .addOnSuccessListener { location: Location? ->
                    if (location != null) {
                        // location found, do something with it
                        val userLatitude = location.latitude
                        val userLongitude = location.longitude
                        var distance = calculateDistance(userLatitude, userLongitude, currentItem.location[0], currentItem.location[1])
                        if (distance < 1.0) {
                            distance *= 1000
                            holder.locationTextView.text = String.format("%.1f m",distance)
                        } else {
                            holder.locationTextView.text = String.format("%.1f km",distance)
                        }
                    } else {
                        // location not found, handle accordingly
                    }
                }
                .addOnFailureListener {
                    // error getting location, handle accordingly
                }
            Glide.with(holder.itemView).load(currentItem.imageUrl.toString()).placeholder(R.drawable.capy_loading_image).into(holder.campsiteImage)
            holder.campsiteName.text = currentItem.name
            holder.capacityTextView.text = currentItem.capacity.toString()
            val reviewHelper = ReviewHelper(currentItemId)
            reviewHelper.populateReviewList {
                holder.campsiteRating.text = String.format("%.1f",reviewHelper.calculateAvg())
            }
            holder.itemView.setOnClickListener {
                val intent = Intent(context, CampsiteDetailsActivity::class.java)
                intent.putExtra("campsiteName", currentItem.name)
                intent.putExtra("imageUrl", currentItem.imageUrl)
                intent.putExtra("ownerUid", currentItem.ownerUID)
                intent.putExtra("campsiteId",currentItemId)
                intent.putExtra("campsiteLocation", currentItem.location)
                context.startActivity(intent)
            }
        }
    }

    private fun calculateDistance(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
        fun deg2rad(deg: Double): Double {
            return deg * (PI / 180)
        }
        val R = 6371 // radius of the earth in km
        val dLat = deg2rad(lat2 - lat1)
        val dLon = deg2rad(lon2 - lon1)
        val a = sin(dLat / 2) * sin(dLat / 2) + cos(deg2rad(lat1)) * cos(deg2rad(lat2)) *
                sin(dLon / 2) * sin(dLon / 2)
        val c = 2 * atan2(sqrt(a), sqrt(1 - a))
        return R * c
    }

    class CampsiteViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView) {
        val campsiteImage : ImageView = itemView.findViewById(R.id.campsiteImage)
        val campsiteName : TextView = itemView.findViewById(R.id.campsiteName)
        val campsiteRating : TextView = itemView.findViewById(R.id.itemListRatingScore)
        val capacityTextView: TextView = itemView.findViewById(R.id.requestCapacityText)
        val locationTextView: TextView = itemView.findViewById(R.id.searchDistanceTxt)

    }
}