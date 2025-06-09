package com.budgetbuddy

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.budgetbuddy.database.AppDatabase
import com.budgetbuddy.database.User
import kotlinx.coroutines.launch

/**
 * MainActivity
 *
 * This is the entry point of the app, providing login and registration functionality.
 * It authenticates users by checking credentials against stored values in the Room database.
 *
 * Author: Muhammad Shaikh
 * With support from: OpenAI ChatGPT (2025)
 * References:
 * - Android Activity Navigation: https://developer.android.com/guide/components/activities/intro-activities
 * - Room Persistence + Coroutines: https://developer.android.com/training/data-storage/room
 */
class MainActivity : AppCompatActivity() {

    // UI elements
    private lateinit var usernameInput: EditText
    private lateinit var passwordInput: EditText
    private lateinit var loginButton: Button
    private lateinit var registerButton: Button

    // RoomDB instance
    private lateinit var db: AppDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Bind UI elements
        usernameInput = findViewById(R.id.usernameInput)
        passwordInput = findViewById(R.id.passwordInput)
        loginButton = findViewById(R.id.loginButton)
        registerButton = findViewById(R.id.registerButton)

        // Initialize database
        db = AppDatabase.getDatabase(this)

        // Set listeners
        loginButton.setOnClickListener { login() }
        registerButton.setOnClickListener { register() }
    }

    /**
     * Handles user login by validating the username and password
     * against stored credentials in RoomDB.
     */
    private fun login() {
        val username = usernameInput.text.toString()
        val password = passwordInput.text.toString()

        if (username.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please fill in both fields.", Toast.LENGTH_SHORT).show()
            return
        }

        lifecycleScope.launch {
            val user = db.userDao().getUser(username)

            // Compare credentials
            if (user != null && user.password == password) {
                Toast.makeText(applicationContext, "Login successful!", Toast.LENGTH_SHORT).show()

                // Navigate to dashboard after successful login
                val intent = Intent(this@MainActivity, DashboardActivity::class.java)
                intent.putExtra("username", username)
                startActivity(intent)
                finish()
            } else {
                Toast.makeText(applicationContext, "Invalid credentials!", Toast.LENGTH_SHORT).show()
            }
        }
    }

    /**
     * Registers a new user if the username is not already taken.
     */
    private fun register() {
        val username = usernameInput.text.toString()
        val password = passwordInput.text.toString()

        if (username.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please fill in both fields.", Toast.LENGTH_SHORT).show()
            return
        }

        lifecycleScope.launch {
            val existingUser = db.userDao().getUser(username)

            if (existingUser != null) {
                Toast.makeText(applicationContext, "Username already taken.", Toast.LENGTH_SHORT).show()
            } else {
                db.userDao().insertUser(User(username, password))
                Toast.makeText(applicationContext, "Registration successful! Please login.", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
