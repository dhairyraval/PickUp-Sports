package com.example.pickupsports

// This is what the user's object is, once received form the db
class User {
    var firstName: String? = null
    var lastName: String? = null
    var dob: String? = null
    var phoneNumber: String? = null
    var uid: String? = null

    constructor(){}

    constructor(dob: String?, firstName: String?, lastName: String?, phoneNumber: String?, uid: String?){
        this.dob = dob
        this.firstName = firstName
        this.lastName = lastName
        this.phoneNumber = phoneNumber
        this.uid = uid
    }

}