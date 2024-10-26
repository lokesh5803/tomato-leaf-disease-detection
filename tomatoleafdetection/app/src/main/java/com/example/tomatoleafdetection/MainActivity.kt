package com.example.tomatoleafdetection

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    private lateinit var emailEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var loginButton: Button
    private lateinit var registerTextView: TextView
    private lateinit var databaseHelper: DatabaseHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        emailEditText = findViewById(R.id.emailEditText)
        passwordEditText = findViewById(R.id.passwordEditText)
        loginButton = findViewById(R.id.loginButton)
        registerTextView = findViewById(R.id.registerTextView)
        databaseHelper = DatabaseHelper(this)

        loginButton.setOnClickListener {
            val email = emailEditText.text.toString()
            val password = passwordEditText.text.toString()
            if (databaseHelper.checkUser(email, password)) {
                Log.d("MainActivity", "Login successful, navigating to DiseaseClassifierActivity")
                val intent = Intent(this, DiseaseClassifierActivity::class.java)
                startActivity(intent)
                finish()
            } else {
                Log.d("MainActivity", "Invalid credentials")
                emailEditText.error = "Invalid credentials"
                passwordEditText.error = "Invalid credentials"
            }
        }

        registerTextView.setOnClickListener {
            val intent = Intent(this, SignupActivity::class.java)
            startActivity(intent)
        }
    }
}
