package com.example.pickupsports.model

import com.google.firebase.database.IgnoreExtraProperties

@IgnoreExtraProperties
data class UserData(
    val phoneNumber: String? = null, val firstName: String? = null, val lastName: String? = null,
    val dob: String? = null, val uid: String?, val notificationToken: String? = null
) {
    // Null default values create a no-argument default constructor, which is needed
    // for deserialization from a DataSnapshot.
}
