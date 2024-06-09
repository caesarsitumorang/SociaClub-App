package com.rpl.sicfo

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.TextView
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.rpl.sicfo.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navView: BottomNavigationView = binding.navView

        val navController = findNavController(R.id.nav_host_fragment_activity_main)

        var lastSelectedItemId = navView.selectedItemId

        navView.setOnNavigationItemSelectedListener { item ->
            // Hide title for the previously selected item
            navView.menu.findItem(lastSelectedItemId)?.title = null

            // Show title for the selected item
            item.title = when (item.itemId) {
                R.id.homeFragment -> "Home"
                R.id.organizerFragment -> "Organizer"
                R.id.votingFragment -> "Voting"
                R.id.profilFragment -> "Profile"
                else -> null
            }

            lastSelectedItemId = item.itemId

            // Manually navigate to the selected fragment
            navController.navigate(item.itemId)

            true
        }
    }
}

