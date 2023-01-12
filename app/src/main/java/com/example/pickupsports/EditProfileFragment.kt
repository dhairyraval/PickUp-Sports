package com.example.pickupsports

import android.app.DatePickerDialog
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.example.pickupsports.databinding.FragmentEditProfileBinding
import com.example.pickupsports.model.UserData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import java.util.*

/**
 * A simple [Fragment] subclass as the default destination in the navigation.
 */
class EditProfileFragment : Fragment() {
    private var TAG: String = "EditProfile"
    private var _binding: FragmentEditProfileBinding? = null
    private lateinit var auth: FirebaseAuth
    private lateinit var database: DatabaseReference

    private lateinit var usernameEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var phoneButton: EditText
    private lateinit var dob: EditText
    private lateinit var fullName: EditText

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
        _binding = FragmentEditProfileBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        usernameEditText = binding.email
        passwordEditText = binding.password
        phoneButton = binding.phoneNumber
        dob = binding.dateOfBirth
        fullName = binding.name

        val loginButton = binding.register

        // Using date picker for birthdate.
        // https://www.geeksforgeeks.org/how-to-popup-datepicker-while-clicking-on-edittext-in-android/
        // Accessed Nov 20, 2022
        dob.setOnClickListener {
            val calendar = Calendar.getInstance()

            val year = calendar.get(Calendar.YEAR)
            val month = calendar.get(Calendar.MONTH)
            val day = calendar.get(Calendar.DAY_OF_MONTH)

            val datePickerDialog = DatePickerDialog(

                requireContext(),
                { _, birthYear, monthOfYear, dayOfMonth ->
                    val dat = (dayOfMonth.toString() + "-" + (monthOfYear + 1) + "-" + birthYear)
                    dob.setText(dat)
                }, year, month, day
            )
            datePickerDialog.show()
        }

        loginButton.setOnClickListener {
            Log.d(TAG, "onClickListener works")
            if(validateFields()) {
                updateUserData(
                    fullName.text.toString(),
                    phoneButton.text.toString(),
                    dob.text.toString()
                )

                // Register success
                Log.d(TAG, "UpdateUserWithEmail:success")
                findNavController().navigate(R.id.action_editProfileFragment_to_profileFragment)
            } else {
                // If register fails, display a message to the user.
                Log.w(TAG, "UpdateUserWithEmail:failure")
                Toast.makeText(
                    activity, "Update failed.",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun validateFields(): Boolean {
        var validated = true
        if(usernameEditText.length() == 0) {
            usernameEditText.error = "Field is required"
            validated = false
        }
        if(passwordEditText.length() == 0) {
            passwordEditText.error = "Field is required"
            validated = false
        }
        if(phoneButton.length() == 0) {
            phoneButton.error = "Field is required"
            validated = false
        }
        if(fullName.length() == 0) {
            fullName.error = "Field is required"
            validated = false
        }
        if(dob.length() == 0) {
            dob.error = "Field is required"
            validated = false
        }
        return validated
    }

    private fun updateUserData(fullName: String, phoneNumber: String, dob: String) {
        val firstName: String = fullName.split(" ")[0]
        val lastName: String = fullName.split(" ")[1]

        val user = UserData(phoneNumber, firstName, lastName, dob, auth.currentUser?.uid)
        val userID = auth.currentUser?.uid
        auth.currentUser?.updateEmail(usernameEditText.text.toString())
        auth.currentUser?.updatePassword(passwordEditText.text.toString())
        userID?.let { database.child("users").child(userID) }?.setValue(user)
        Log.w(TAG, "email:${usernameEditText.text} pass:${passwordEditText.text}")
    }

}
