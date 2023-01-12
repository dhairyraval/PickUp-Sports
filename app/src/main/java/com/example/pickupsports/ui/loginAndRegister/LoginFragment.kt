package com.example.pickupsports.ui.loginAndRegister

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
import com.example.pickupsports.databinding.FragmentLoginBinding
import com.google.firebase.auth.FirebaseAuth

/**
 * Login page for use with firebase auth.
 */
class LoginFragment : Fragment() {
    private var TAG: String = "Login"
    private var _binding: FragmentLoginBinding? = null
    private lateinit var auth: FirebaseAuth

    private lateinit var usernameEditText: EditText
    private lateinit var passwordEditText: EditText

    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        auth = FirebaseAuth.getInstance()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        usernameEditText = binding.username
        passwordEditText = binding.password
        val loginButton = binding.login
        val registerButton = binding.createAccount


        //Login flow with client and serverside validation
        loginButton.setOnClickListener {
            if(validateFields()) {
                auth.signInWithEmailAndPassword(
                    usernameEditText.text.toString(),
                    passwordEditText.text.toString()
                ).addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        // Sign in success, update UI with the signed-in user's information
                        Log.d(TAG, "signInWithEmail:success")
                        findNavController().navigate(R.id.action_loginFragment_to_HomeFragment)

                        
                    } else {
                        // If sign in fails, display a message to the user.
                        Log.w(TAG, "signInWithEmail:failure", task.exception)
                        Toast.makeText(
                            context, "Authentication failed.",
                            Toast.LENGTH_SHORT
                        ).show()

                    }
                }
            }
        }

        registerButton.setOnClickListener {
            findNavController().navigate(R.id.action_loginFragment_to_registerFragment)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    //Checking edit text for valid input
    private fun validateFields(): Boolean {
        var validated = true
        if(!Patterns.EMAIL_ADDRESS.matcher(usernameEditText.text).matches()) {
            usernameEditText.error = "Valid email is required"
            validated = false
        }
        if(passwordEditText.length() < 6) {
            passwordEditText.error = "Minimum of 6 characters"
            validated = false
        }
        return validated
    }
}
