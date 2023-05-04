package com.shaygang.campybara

import java.util.*

class Reservation (val campsiteId : String, val campsiteOwnerId : String,
                   val requestingUserId : String, val requestingGroupId : String,
                   val reservationFromDate : Date, val reservationToDate : Date,
                   val nbOfPeople : Int, val reservationState : ReservationState)

enum class ReservationState(val value: Int) {
    PENDING(0),
    ACCEPTED(1),
    REJECTED(2)
}
