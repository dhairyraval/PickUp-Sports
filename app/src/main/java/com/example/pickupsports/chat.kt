package com.example.pickupsports

// This is what the chat object looks like, only the message and the sender's id
class chat {
    var message: String? = null
    var senderId: String? = null

    constructor(){}

    constructor(message: String?, senderId: String?){
        this.message = message
        this.senderId = senderId
    }
}