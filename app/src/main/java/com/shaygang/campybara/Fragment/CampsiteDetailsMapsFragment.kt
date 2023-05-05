package com.shaygang.campybara.Fragment

import androidx.fragment.app.Fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.shaygang.campybara.Activity.CampsiteDetailsActivity
import com.shaygang.campybara.R

class CampsiteDetailsMapsFragment : Fragment() {

    private val callback = OnMapReadyCallback { googleMap ->
        val location = LatLng(CampsiteDetailsActivity.CAMPSITE_LOCATION[0], CampsiteDetailsActivity.CAMPSITE_LOCATION[1])
        googleMap.uiSettings.isZoomControlsEnabled = true
        googleMap.uiSettings.isScrollGesturesEnabled = false
        googleMap.uiSettings.isZoomGesturesEnabled = false
        googleMap.uiSettings.isRotateGesturesEnabled = false
        googleMap.addMarker(MarkerOptions().position(location).title("Campsite Location"))
        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(location, 15f))
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_maps, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync(callback)
    }
}