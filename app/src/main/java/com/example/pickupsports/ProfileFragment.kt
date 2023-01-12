package com.example.pickupsports

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.example.pickupsports.databinding.FragmentProfileBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

/**
 * Profile fragment for viewing one's account data.
 */
class ProfileFragment : Fragment() {
    private var TAG: String = "Profile"
    private var _binding: FragmentProfileBinding? = null
    private lateinit var auth: FirebaseAuth
    private lateinit var database: DatabaseReference

    private lateinit var emailTextView: TextView
    private lateinit var phoneButton: TextView
    private lateinit var dob: TextView
    private lateinit var fullName: TextView

    private lateinit var userID: String
    private lateinit var userDob: String
    private lateinit var userName: String
    private lateinit var userPhoneNumber: String

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        auth = FirebaseAuth.getInstance()
        database = Firebase.database.reference

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        emailTextView = binding.email
        phoneButton = binding.phoneNumber
        dob = binding.dateOfBirth
        fullName = binding.name
        val editButton = binding.editProfileBtn
        val logoutButton = binding.logoutBtn
        database.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                getUserData()
            }
            override fun onCancelled(error: DatabaseError) {
                Log.w(TAG, "Failed to read value.", error.toException())
            }

        })

        editButton.setOnClickListener {
            findNavController().navigate(R.id.action_profileFragment_to_editProfileFragment)
        }

        // logout
        logoutButton.setOnClickListener {
            auth.signOut()
            findNavController().navigate(R.id.action_profileFragment_to_loginFragment)
            Toast.makeText(activity, "You are Logged Out!", Toast.LENGTH_LONG).show()
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun getUserData() {
        userID = auth.currentUser!!.uid
        database.child("users").child(userID).get().addOnSuccessListener {
            if (it.exists()){
                userName = it.child("firstName").value.toString() + " " + it.child("lastName").value.toString()
                userDob = it.child("dob").value.toString()
                userPhoneNumber = it.child("phoneNumber").value.toString()
                Log.w(TAG, "userN:$userName DOB:$userDob phoneNum:$userPhoneNumber")

                setUserData()

            } else {
                Log.w(TAG, "User Information not found.")
            }
        }
    }

    private fun setUserData() {
        emailTextView.text = auth.currentUser!!.email.toString()
        phoneButton.text = userPhoneNumber
        dob.text = userDob
        fullName.text = userName
    }
}

