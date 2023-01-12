package com.example.pickupsports

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.ktx.Firebase

class ChatActivity : AppCompatActivity() {
    private lateinit var chatRecyclerView: RecyclerView
    private lateinit var messageBox: EditText
    private lateinit var sendButton: ImageView
    private lateinit var messageAdapter: MessageAdapter
    private lateinit var messageList: ArrayList<chat>
    private lateinit var mDbRef: DatabaseReference

    // Creating two spaces for sender and the receiver to save two copies of the message
    // cause we need to show it to the sender and the receiver as well
    var receiverRoom: String? = null
    var senderRoom: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)

        // Getting the name and the uid of the receiver
        val name = intent.getStringExtra("name")
        val receiverUid = intent.getStringExtra("uid")

        // get the uid if the user that is logged in/the sender
        val senderUid = FirebaseAuth.getInstance().currentUser?.uid

        mDbRef = FirebaseDatabase.getInstance().getReference()

        // what each space should contain
        senderRoom = receiverUid + senderUid
        receiverRoom = senderUid + receiverUid

        // Displaying the name of the receiver at the top of the chat
        supportActionBar?.title = name

        chatRecyclerView = findViewById(R.id.chatRecyclerView)
        messageBox = findViewById(R.id.messageBox)
        sendButton = findViewById(R.id.sendButton)

        // Initializing the message object and the arrayList
        messageList = ArrayList()
        messageAdapter = MessageAdapter(this,messageList)

        //Displaying the message in the chat
        chatRecyclerView.layoutManager = LinearLayoutManager(this)
        chatRecyclerView.adapter = messageAdapter

        //Fetching the messages from the database
        mDbRef.child("chats").child(senderRoom!!).child("messages")
            .addValueEventListener(object: ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    messageList.clear()
                    for(postSnapshot in snapshot.children){
                        val message = postSnapshot.getValue(chat::class.java)
                        messageList.add(message!!)
                    }
                    messageAdapter.notifyDataSetChanged()

                }
                override fun onCancelled(error: DatabaseError) {
                }
            })

        // setting a onClickListener on the send button
        sendButton.setOnClickListener{

            val message = messageBox.text.toString()
            val messageObject = chat(message,senderUid)

            //Creating a message table to store the values of the messages according to the room
            mDbRef.child("chats").child(senderRoom!!).child("messages").push()
                .setValue(messageObject).addOnSuccessListener {
                    mDbRef.child("chats").child(receiverRoom!!).child("messages").push()
                        .setValue(messageObject)
                }
            messageBox.setText("")
        }
    }
}