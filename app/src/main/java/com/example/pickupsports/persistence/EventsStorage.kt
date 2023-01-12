package com.example.pickupsports.persistence

import android.content.ContentValues.TAG
import android.util.Log
import com.example.pickupsports.model.Event
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase

/**
 * Retrieve event data from db and store them into the [events] ArrayList
 */
object EventsStorage {

    //var database: DatabaseReference = Firebase.database.reference
    var events = ArrayList<Event>()

    init {

        //getEvents()
        Log.w(TAG, "ArraySize: " + events.size)
    }

}