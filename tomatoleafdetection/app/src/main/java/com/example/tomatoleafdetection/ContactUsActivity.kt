package com.example.tomatoleafdetection

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView

class ContactUsActivity : AppCompatActivity() {

    private lateinit var nameField: EditText
    private lateinit var emailField: EditText
    private lateinit var messageField: EditText
    private lateinit var buttonSubmit: Button
    private lateinit var toolbar: Toolbar
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navigationView: NavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.contact_us)

        // Initialize views
        toolbar = findViewById(R.id.toolbar)
        drawerLayout = findViewById(R.id.drawer_layout)
        nameField = findViewById(R.id.nameField)
        emailField = findViewById(R.id.emailField)
        messageField = findViewById(R.id.messageField)
        buttonSubmit = findViewById(R.id.buttonSubmit)
        navigationView = findViewById(R.id.navigation_view)

        // Set up the toolbar
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        // Set up navigation drawer
        navigationView.setNavigationItemSelectedListener { menuItem ->
            handleNavigationItemSelected(menuItem)
            drawerLayout.closeDrawers() // Close the drawer after selecting an item
            true
        }

        // Set up button submit click listener
        buttonSubmit.setOnClickListener {
            handleSubmit()
        }
    }

    // Handle navigation drawer item clicks
    private fun handleNavigationItemSelected(menuItem: MenuItem): Boolean {
        when (menuItem.itemId) {
            R.id.nav_home -> {
                startActivity(Intent(this, DiseaseClassifierActivity::class.java))
                finish() // Finish current activity
            }
            R.id.nav_about_us -> {
                startActivity(Intent(this, AboutUsActivity::class.java))
                finish() // Finish current activity
            }
            R.id.nav_exit -> {
                // Log out logic or close the app
                finishAffinity() // This closes the app
            }
            else -> {
                Toast.makeText(this, "Already on Contact Us", Toast.LENGTH_SHORT).show()
            }
        }
        return true
    }

    // Handle the submit button click
    private fun handleSubmit() {
        val name = nameField.text.toString()
        val email = emailField.text.toString()
        val message = messageField.text.toString()

        if (name.isBlank() || email.isBlank() || message.isBlank()) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, "Message submitted!", Toast.LENGTH_SHORT).show()
            nameField.text.clear()
            emailField.text.clear()
            messageField.text.clear()
        }
    }

    // Handle back button in toolbar
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return if (item.itemId == android.R.id.home) {
            onBackPressed()
            true
        } else {
            super.onOptionsItemSelected(item)
        }
    }
}
