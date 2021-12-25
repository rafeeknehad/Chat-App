package com.example.myapplication

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.setupActionBarWithNavController

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val navController = findNavController(R.id.fragmentContainerView)
        setupActionBarWithNavController(navController)
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.fragmentContainerView)
        return navController.navigateUp() || super.onSupportNavigateUp()
    }

    companion object {
        const val SHARD_REFERENCE = "WhatsApp"
        const val EMAIL = "Email"
        const val DISPLAY_NAME = "DisplayName"
        const val NUMBER = "Number"
        const val PASSWORD = "Password"
        const val ID = "Id"
        const val PROFILE_IMAGE = "profileImage"
        const val TOKEN = "token"

    }
}