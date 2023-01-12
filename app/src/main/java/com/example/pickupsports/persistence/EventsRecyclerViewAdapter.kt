package com.example.pickupsports.persistence

import android.content.ContentValues.TAG
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment.Companion.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.example.pickupsports.R
import com.example.pickupsports.model.Event
import com.example.pickupsports.model.UserData
import com.firebase.ui.auth.data.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class EventsRecyclerViewAdapter() : RecyclerView.Adapter<EventsRecyclerViewAdapter.EventListItem>() {
    private var events = emptyList<Event>()

    inner class EventListItem(eventListItemView : View?) : RecyclerView.ViewHolder(eventListItemView!!){
        val eventCard : CardView? = eventListItemView?.findViewById(R.id.eventCard)
        val eventTitleText : TextView? = eventListItemView?.findViewById(R.id.eventTitle)
        val eventIcon : ImageView? = eventListItemView?.findViewById(R.id.sportIcon)
        val eventLocation: TextView? = eventListItemView?.findViewById(R.id.eventLocation)
        val eventTime: TextView? = eventListItemView?.findViewById(R.id.eventTime)
        val eventDate: TextView? = eventListItemView?.findViewById(R.id.eventDate)
        val availability: TextView? = eventListItemView?.findViewById(R.id.numberOfPlayer)
        val levelOfPlay: TextView? = eventListItemView?.findViewById(R.id.levelOfPlay)
        val userID = FirebaseAuth.getInstance().currentUser?.uid

        var eventPosition = 0

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EventListItem {
        val layoutInflater : LayoutInflater = LayoutInflater.from(parent.context)
        val eventListView : View = layoutInflater.inflate(R.layout.event_view, parent, false)
        return EventListItem(eventListView)
    }

    override fun onBindViewHolder(holder: EventListItem, position: Int) {
        val event : Event = events[position]
        holder.eventTitleText?.text = event.sportName
        // set event icon accordingly
        holder.eventIcon?.setImageResource(getIcon(event.sportName))

        // set time, date, location, availability and level of play
        holder.eventTime?.text = event.time
        holder.eventDate?.text = event.date
        holder.eventLocation?.text = event.locationText
        holder.availability?.text = "Spots: " + event.currentPlayer.toString() + "/" + event.capacity.toString()
        holder.levelOfPlay?.text = "Lv: " + event.levelOfPlay
        // click to view event summary
        val bundle = Bundle()
        bundle.putString("referer", "home")
        bundle.putString("eventId", event.eventId)


        Firebase.database.reference.child("events").child(event.eventId.toString()).child("owner").child("uid").get().addOnSuccessListener {
            bundle.putString("ownerId", it.value.toString())
        }

        holder.eventCard?.setOnClickListener {
            it.findNavController().navigate(R.id.SummaryFragment, bundle)
        }
    }

    override fun getItemCount(): Int {
        return events.size
    }

    fun setEvents(events : List<Event>){
        this.events = events;
        notifyDataSetChanged()
    }

    /**
     * Take in [eventName] and get the corresponding icon name
     * for demo purpose, only 5 events are selectable (and plus one default icon)
     * @return icon
     */
    fun getIcon(eventName: String?): Int {
        return when {
            eventName.equals("tennis",true) -> R.drawable.tennis_icon
            eventName.equals("football",true) -> R.drawable.football_icon
            eventName.equals("basketball",true) -> R.drawable.basketball_icon
            eventName.equals("cycling",true) -> R.drawable.cycling_icon
            eventName.equals("baseball",true) -> R.drawable.baseball_icon
            else -> R.drawable.default_icon
        }
    }


}