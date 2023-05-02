package com.shaygang.campybara

import java.util.*

class ReservationRequest (val campsiteId : String, val campsiteOwnerId : String, val requestingUserId : String, val requestingGroupId : String, val reservationFromDate : Date, val reservationToDate : Date, val nbOfPeople : Int) {
}