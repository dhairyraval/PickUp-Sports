package com.example.pickupsports.model

import com.google.android.gms.maps.model.LatLng

/**
 * An event object
 */
data class Event (
    val owner: UserData? = null,
    val eventId: String? = null,
    val locationText: String? = null,
    val location: LatLng? = null,
    val date: String? = null,
    val time: String? = null,
    val sportName: String? = null,
    val capacity: Int? = null,
    var currentPlayer: Int? = null,
    val levelOfPlay: String? = null,
    val notice: String? = null,
)