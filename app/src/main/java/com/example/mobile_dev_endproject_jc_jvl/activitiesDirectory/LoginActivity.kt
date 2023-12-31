package com.example.mobile_dev_endproject_jc_jvl.activitiesDirectory

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.mobile_dev_endproject_jc_jvl.R
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth

class LoginActivity : AppCompatActivity() {

    private lateinit var emailEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var forgotPasswordTextView: TextView
    private lateinit var loginButton: Button
    private lateinit var registerButton: Button

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.login_screen)

        auth = FirebaseAuth.getInstance()

        emailEditText = findViewById(R.id.emailEditText)
        passwordEditText = findViewById(R.id.passwordEditText)
        forgotPasswordTextView = findViewById(R.id.forgotPasswordTextView)
        loginButton = findViewById(R.id.loginButton)
        registerButton = findViewById(R.id.registerButton)

        loginButton.setOnClickListener {
            val email = emailEditText.text.toString().trim()
            val password = passwordEditText.text.toString().trim()

            if (email.isNotEmpty() && password.isNotEmpty()) {
                loginUser(email, password)
            } else {
                // empty fields
                if (email.isEmpty()) {
                    emailEditText.setBackgroundResource(R.drawable.edit_text_error_border)
                } else {
                    emailEditText.setBackgroundResource(R.drawable.edit_text_default_border)
                }

                if (password.isEmpty()) {
                    passwordEditText.setBackgroundResource(R.drawable.edit_text_error_border)
                } else {
                    passwordEditText.setBackgroundResource(R.drawable.edit_text_default_border)
                }
            }
        }

        forgotPasswordTextView.setOnClickListener {
            // Handle forgot password
        }

        registerButton.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }
    }

    private fun loginUser(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Login successful
                    startActivity(Intent(this, HomeActivity::class.java))
                } else {
                    // If login fails, display a message to the user.
                    // You can customize the error message based on the task.exception
                    // For example, task.exception?.message
                    // Handle authentication errors
                }
                val errorMessage = task.exception?.message ?: "Login failed"
                showSnackbar(errorMessage)

                // Set red contour for email and password fields
                emailEditText.setBackgroundResource(R.drawable.edit_text_error_border)
                passwordEditText.setBackgroundResource(R.drawable.edit_text_error_border)
            }
    }

    private fun showSnackbar(message: String) {
        // Assuming your root view is a CoordinatorLayout, replace it with the appropriate view type if needed
        val rootView = findViewById<View>(android.R.id.content)
        Snackbar.make(rootView, message, Snackbar.LENGTH_LONG).show()
    }
}