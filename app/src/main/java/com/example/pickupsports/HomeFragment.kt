package com.example.pickupsports

import android.content.ContentValues
import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.cardview.widget.CardView
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.pickupsports.databinding.FragmentHomeBinding
import com.example.pickupsports.model.Event
import com.example.pickupsports.persistence.EventsRecyclerViewAdapter
import com.example.pickupsports.persistence.EventsStorage
import com.example.pickupsports.model.UserData
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.database.*
import com.google.firebase.ktx.Firebase

/**
 * A simple [Fragment] subclass as the default destination in the navigation.
 */
class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null

    private lateinit var dbref: DatabaseReference
    private lateinit var eventList: ArrayList<Event>
    // This property is only valid between onCreateView and
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        eventList = arrayListOf<Event>()

        val recylerView : RecyclerView = view.findViewById(R.id.eventRV)
        recylerView.layoutManager = LinearLayoutManager(activity)

        val recyclerViewAdapter = EventsRecyclerViewAdapter()
        recylerView.adapter = recyclerViewAdapter
        getEvents(recyclerViewAdapter)
        Log.i(TAG, "events: " + EventsStorage.events)
    }
    // onDestroyView.
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    /**
     * get all exist events from db, return an ArrayList of events
     */
    private fun getEvents(adapter: EventsRecyclerViewAdapter) {
        dbref = FirebaseDatabase.getInstance().getReference("events")

        dbref.addValueEventListener(object : ValueEventListener {

            override fun onDataChange(snapshot: DataSnapshot) {
                eventList.clear()
                if (snapshot.exists()) {

                    for (eventSnapshot in snapshot.children) {
                        eventList.add(buildEvent(eventSnapshot))
                    }
                    adapter.setEvents(eventList)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                // do nothing
            }
        })

    }

    /**
     * helper function
     * build a single Event entity and return it
     */
    private fun buildEvent(entry: DataSnapshot): Event {
        // event info
        val owner: UserData? = null
        val eventId: String? = entry.key
        val locationText: String? = entry.child("locationText").value.toString()
        val location: LatLng? = null
        val time: String? = entry.child("time").value.toString()
        val date: String? = entry.child("date").value.toString()
        val sportName: String? = entry.child("sportName").value.toString()
        val capacity: Int? = entry.child("capacity").value.toString().toInt()
        val currPlayer: Int? = entry.child("currentPlayer").value.toString().toInt()
        val levelOfPlay: String? = entry.child("levelOfPlay").value.toString()
        val notice: String? = entry.child("notice").value.toString()

        return Event(owner, eventId, locationText, location,date, time, sportName, capacity, currPlayer, levelOfPlay, notice)
    }

}