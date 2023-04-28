package com.shaygang.campybara

import android.location.Location

class Campsite(val name: String, val description: String? = "No description provided", val capacity: Int? = -1, val imageUrl: String?, val rating: Double? = 0.0, val ownerUID: String, val location: Location) {}