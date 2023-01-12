package com.example.pickupsports

import android.content.ContentValues
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.pickupsports.databinding.FragmentHomeBinding
import com.example.pickupsports.databinding.FragmentUpcomingBinding
import com.example.pickupsports.model.Event
import com.example.pickupsports.model.UserData
import com.example.pickupsports.persistence.EventsRecyclerViewAdapter
import com.example.pickupsports.persistence.EventsStorage
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*


/**
 * A simple [Fragment] subclass.
 * Use the [UpcomingFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class UpcomingFragment : Fragment() {
    private var _binding: FragmentUpcomingBinding? = null

    private lateinit var dbref: DatabaseReference
    private lateinit var eventList: ArrayList<Event>
    // This property is only valid between onCreateView and
    private val binding get() = _binding!!
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentUpcomingBinding.inflate(inflater, container, false)

        auth = FirebaseAuth.getInstance()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        eventList = arrayListOf<Event>()

        val recylerView : RecyclerView = view.findViewById(R.id.eventUpcomingRV)
        recylerView.layoutManager = LinearLayoutManager(activity)

        val recyclerViewAdapter = EventsRecyclerViewAdapter()
        recylerView.adapter = recyclerViewAdapter
        getUpcomingEvents(recyclerViewAdapter)
        Log.i(ContentValues.TAG, "events: " + EventsStorage.events)
    }

    /**
     * get all events that current user is registered as a participant in.
     */
    private fun getUpcomingEvents(adapter: EventsRecyclerViewAdapter) {
        dbref = FirebaseDatabase.getInstance().reference

        dbref.child("events").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    Log.d("Event Upcoming", snapshot.value.toString())
                    for (eventSnapshot in snapshot.children) {
                        val eventKey = eventSnapshot.key
                        dbref.child("participants/${eventKey}/${auth.currentUser!!.uid}").addListenerForSingleValueEvent(object : ValueEventListener {
                            override fun onDataChange(snapshot: DataSnapshot) {
                                if(snapshot.exists()) {
                                    eventList.add(buildEvent(eventSnapshot))
                                    adapter.setEvents(eventList)
                                }
                            }

                            override fun onCancelled(error: DatabaseError) {
                                // do nothing
                            }

                        })
                    }

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