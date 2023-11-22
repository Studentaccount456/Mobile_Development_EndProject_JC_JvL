package com.example.mobile_dev_endproject_jc_jvl

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth

class RegisterActivity : AppCompatActivity() {

    private lateinit var emailEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var usernameEditText: EditText
    private lateinit var submitButton: Button
    private lateinit var returnButton: Button

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        auth = FirebaseAuth.getInstance()

        emailEditText = findViewById(R.id.emailRegisterEditText)
        passwordEditText = findViewById(R.id.passwordRegisterEditText)
        usernameEditText = findViewById(R.id.usernameEditText)
        submitButton = findViewById(R.id.submitButton)
        returnButton = findViewById(R.id.returnButton)

        submitButton.setOnClickListener {
            val email = emailEditText.text.toString().trim()
            val password = passwordEditText.text.toString().trim()
            val username = usernameEditText.text.toString().trim()

            if (email.isNotEmpty() && password.isNotEmpty() && username.isNotEmpty()) {
                registerUser(email, password)
                finish()
            } else {
                // Handle empty fields
                showSnackbar("Please fill in all fields.")
            }
        }

        returnButton.setOnClickListener {
            // Return to LoginActivity
            finish() // Close the RegisterActivity and return to the LoginActivity
        }
    }

    private fun registerUser(email: String, password: String) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Registration successful
                    // You can navigate to the main activity or perform other actions
                    showSnackbar("Registration successful!")
                } else {
                    // If registration fails, display a message to the user.
                    // You can customize the error message based on the task.exception
                    // For example, task.exception?.message
                    // Handle registration errors
                    val errorMessage = task.exception?.message ?: "Registration failed"
                    showSnackbar(errorMessage)
                }
            }
    }

    private fun showSnackbar(message: String) {
        // Assuming your root view is a CoordinatorLayout, replace it with the appropriate view type if needed
        val rootView = findViewById<View>(android.R.id.content)
        Snackbar.make(rootView, message, Snackbar.LENGTH_LONG).show()
    }
}
