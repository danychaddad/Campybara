package com.shaygang.campybara.Adapter

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
import com.shaygang.campybara.Class.Campsite
import com.shaygang.campybara.R
import kotlin.math.*

class SearchAdapter(private val campsiteIds : List<String>, private val context : Context) : RecyclerView.Adapter<SearchAdapter.ViewHolder>() {
    // Define a ViewHolder class to hold the views for each item
    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val nameTextView: TextView = view.findViewById(R.id.searchCampsiteName)
        val locationTextView: TextView = view.findViewById(R.id.searchDistanceTxt)
        val imageView: ImageView = view.findViewById(R.id.searchCampsiteImage)
        val capacityTextView: TextView = view.findViewById(R.id.requestCapacityText)
    }

    // Inflate the item layout and return a ViewHolder object
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.search_item, parent, false)
        return ViewHolder(view)
    }

    // Set the values of the views in the ViewHolder based on the data at the specified position
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        // TODO: Fix campsite description, stretch image, and make search work
        Campsite.getCampsiteFromId(campsiteIds[position]) {
            val currentItem = it!!
            holder.nameTextView.text = currentItem.name
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
            Glide.with(holder.imageView).load(currentItem.imageUrl)
                .placeholder(R.drawable.capy_loading_image).into(holder.imageView)
            holder.capacityTextView.text = currentItem.capacity.toString()
            holder.itemView.setOnClickListener {
                val intent = Intent(context, CampsiteDetailsActivity::class.java)
                intent.putExtra("campsiteName", currentItem.name)
                intent.putExtra("imageUrl", currentItem.imageUrl)
                intent.putExtra("ownerUid", currentItem.ownerUID)
                intent.putExtra("campsiteId",campsiteIds[position])
                intent.putExtra("campsiteLocation", currentItem.location)
                context.startActivity(intent)
            }
        }
    }

    // Return the number of items in the dataset
    override fun getItemCount(): Int {
        return campsiteIds.size
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

}