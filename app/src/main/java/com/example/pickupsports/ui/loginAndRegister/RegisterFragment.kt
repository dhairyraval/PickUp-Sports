package com.example.pickupsports.ui.loginAndRegister

import android.app.DatePickerDialog
import androidx.fragment.app.Fragment
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.example.pickupsports.R

import com.example.pickupsports.databinding.FragmentRegisterBinding
import com.example.pickupsports.model.UserData
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.ktx.messaging
import java.util.*


/**
 * Fragment for creating new users with Firebase Auth and storing additional
 * user fields in realtime database
 */
class RegisterFragment : Fragment() {
    private var TAG: String = "Register"
    private var _binding: FragmentRegisterBinding? = null
    private lateinit var auth: FirebaseAuth
    private lateinit var database: DatabaseReference

    private lateinit var usernameEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var phoneNumberText: EditText
    private lateinit var dob: EditText
    private lateinit var fullName: EditText

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
        _binding = FragmentRegisterBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        usernameEditText = binding.username
        passwordEditText = binding.password
        phoneNumberText = binding.phoneNumber
        dob = binding.dateOfBirth
        fullName = binding.name

        val registerButton = binding.register
        val loginButton = binding.backToLogin

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

        registerButton.setOnClickListener {
            if(validateFields()) {
                //Create new account with Firebase Auth
                auth.createUserWithEmailAndPassword(
                    usernameEditText.text.toString(),
                    passwordEditText.text.toString()
                ).addOnCompleteListener() { task ->
                    if (task.isSuccessful) {
                        //Subscribe to event notification channel
                        notificationSetup()

                        //Add user data to database
                        uploadUserData(fullName.text.toString(), phoneNumberText.text.toString(), dob.text.toString())

                        //Inform user of registration success.
                        Toast.makeText(
                            context, "Successfully created account!",
                            Toast.LENGTH_SHORT
                        ).show()

                        findNavController().navigate(R.id.action_registerFragment_to_loginFragment)

                    } else {
                        // If register fails, display a message to the user.
                        Log.w(TAG, "createUserWithEmail:failure", task.exception)
                        Toast.makeText(
                            activity, "Registration failed.",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
        }

        loginButton.setOnClickListener {
            findNavController().navigate(R.id.action_registerFragment_to_loginFragment)
        }


    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    //Check if each field is filled out correctly.
    fun validateFields(): Boolean {
        var validated = true
        if(!Patterns.EMAIL_ADDRESS.matcher(usernameEditText.text).matches()) {
            usernameEditText.error = "Valid email is required"
            validated = false
        }
        if(passwordEditText.length() < 6) {
            passwordEditText.error = "Minimum of 6 characters"
            validated = false
        }
        if(!Patterns.PHONE.matcher(phoneNumberText.text).matches()) {
            phoneNumberText.error = "Field is required"
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

    //Create user object and store it in database
    fun uploadUserData(fullName: String, phoneNumber: String, dob: String) {
        val firstName: String = fullName.split(" ")[0]
        val lastName: String = fullName.split(" ")[1]
        Log.d(TAG, "userdata: PH:$phoneNumber FN:$firstName LN:$lastName DOB:$dob")

        val user = UserData(phoneNumber, firstName, lastName, dob, auth.currentUser?.uid)
        val userID = auth.currentUser?.uid
        userID?.let { database.child("users").child(it) }?.setValue(user)
    }

    // Opt-into push notifications for new events, don't need explicit user agreement as per
    // developer guidelines
    private fun notificationSetup() {
        FirebaseMessaging.getInstance().token.addOnCompleteListener(
            OnCompleteListener { task ->
            if (!task.isSuccessful) {
                Log.w(TAG, "Fetching FCM registration token failed", task.exception)
                return@OnCompleteListener
            }

            Firebase.messaging.subscribeToTopic("events")
                .addOnCompleteListener { task ->
                    var msg = "Subscribed"
                    if (!task.isSuccessful) {
                        msg = "Subscribe failed"
                    }
                    Log.d(TAG, msg)
                }
        })
    }
}
