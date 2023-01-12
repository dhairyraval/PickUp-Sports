package com.example.pickupsports

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.navigation.Navigation.findNavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth


class UserAdapter(private val userList: ArrayList<User>):
    RecyclerView.Adapter<UserAdapter.UserViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val view: View = LayoutInflater.from(parent.context).inflate(R.layout.user_layout, parent, false)
        return UserViewHolder(view)
    }

    //bind the view and fetch the user's firstName and the uid and also pass it to the ChatActivity
    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        val currUser = userList[position]
        holder.txtName.text = currUser.firstName

        //Once the user clicks on the user which they want to chat, it will fetch firstName and the uid accordingly
        holder.itemView.setOnClickListener{
            val intent = Intent(it.context,ChatActivity::class.java)
            intent.putExtra("name",currUser.firstName)
            intent.putExtra("uid", currUser.uid)
            it.context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
        return userList.size
    }

    class UserViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        val txtName = itemView.findViewById<TextView>(R.id.txt_name)
    }
}