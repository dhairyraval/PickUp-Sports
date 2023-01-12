package com.example.pickupsports

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.pickupsports.databinding.ActivityMainActivtyBinding
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class MainActivty : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainActivtyBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var navController: NavController


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainActivtyBinding.inflate(layoutInflater)
        setContentView(binding.root)

        navController = findNavController(R.id.nav_host_fragment_content_main_activty)
        appBarConfiguration = AppBarConfiguration(navController.graph)

        auth = FirebaseAuth.getInstance()

        // Getting the bottomNavigation
        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottomNavigationView)

        //Depending on the fragment the user is, display or hide the navigation bar
        //Hide is on the login and the register page and display it for the rest
        navController.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                R.id.loginFragment-> {
                    bottomNavigationView.visibility = View.GONE
                }
                R.id.registerFragment -> {
                    bottomNavigationView.visibility = View.GONE
                }
                else -> {
                    bottomNavigationView.visibility = View.VISIBLE
                }
            }
        }
        //call the Navigation change function
        changeFragmentOnSelect()
    }

    fun changeFragmentOnSelect(){
        val bundle = Bundle()
        bundle.putString("mode", "create")

        //Depending on which option is selected on the bottom nav, navigate it respective fragment
        binding.bottomNavigationView.setOnItemSelectedListener {

            when(it.itemId){
                R.id.home -> navController.navigate(R.id.HomeFragment)
                R.id.message ->  navController.navigate(R.id.MessageFragment)
                R.id.create -> navController.navigate(R.id.CreateEvent, bundle)
                R.id.upcoming -> navController.navigate(R.id.UpcomingFragment)
                R.id.acc -> navController.navigate(R.id.profileFragment)

                else->{

                }
            }

            true
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_main_activty)
        return navController.navigateUp(appBarConfiguration)
                || super.onSupportNavigateUp()
    }
}