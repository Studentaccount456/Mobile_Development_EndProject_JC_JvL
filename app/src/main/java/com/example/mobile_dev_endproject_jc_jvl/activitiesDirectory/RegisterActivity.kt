package com.example.mobile_dev_endproject_jc_jvl.activitiesDirectory

import android.content.Intent
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import androidx.appcompat.app.AppCompatActivity
import com.example.mobile_dev_endproject_jc_jvl.R
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth

class RegisterActivity : AppCompatActivity() {

    private lateinit var emailEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var usernameEditText: EditText
    private lateinit var genderOfPlayerSpinner: Spinner
    private lateinit var submitButton: Button
    private lateinit var returnButton: Button

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.register_screen)

        auth = FirebaseAuth.getInstance()

        emailEditText = findViewById(R.id.emailRegisterEditText)
        passwordEditText = findViewById(R.id.passwordRegisterEditText)
        usernameEditText = findViewById(R.id.usernameEditText)
        genderOfPlayerSpinner = findViewById(R.id.chooseGenderSpinner)
        submitButton = findViewById(R.id.submitButton)
        returnButton = findViewById(R.id.returnButton)

        submitButton.setOnClickListener {
            val email = emailEditText.text.toString().trim()
            val password = passwordEditText.text.toString().trim()
            val username = usernameEditText.text.toString().trim()
            val gender = genderOfPlayerSpinner.selectedItem.toString()

            if (email.isNotEmpty() && password.isNotEmpty() && username.isNotEmpty()) {
                registerUser(email, password, gender)
            } else {
                // Check which fields are empty and display corresponding error messages
                when {
                    email.isEmpty() -> {
                        emailEditText.error = "Email cannot be empty"
                    }

                    password.isEmpty() -> {
                        passwordEditText.error = "Password cannot be empty"
                    }

                    username.isEmpty() -> {
                        usernameEditText.error = "Username cannot be empty"
                    }
                }
                showSnackbar("Please fill in all fields.")
            }
        }

        returnButton.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
        }
    }

    private fun registerUser(email: String, password: String, gender: String) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val user = auth.currentUser
                    user?.let {
                        val userId = user.uid
                        val username = usernameEditText.text.toString().trim()
                        // Add user to "ThePlayers" collection
                        val player = hashMapOf(
                            "userId" to userId,
                            "email" to email,
                            "username" to usernameEditText.text.toString().trim(),
                            "gender" to gender
                        )

                        val db = Firebase.firestore
                        val sanitizedUsername = username.replace("[\\s,\\\\/]".toRegex(), "")

                        db.collection("ThePlayers")
                            .document(userId)
                            .set(player)
                            .addOnSuccessListener {
                                // After success create sub-collection "TheProfileDetails" and its document
                                val profileDetails = hashMapOf(
                                    "Avatar" to "https://firebasestorage.googleapis.com/v0/b/mobile-development4.appspot.com/o/default_image.jpg?alt=media",
                                    "Followers" to 0,
                                    "Following" to 0,
                                    "Level" to 1,
                                    "Username" to usernameEditText.text.toString().trim(),
                                    "Gender" to gender
                                )

                                db.collection("ThePlayers")
                                    .document(userId)
                                    .collection("TheProfileDetails")
                                    .document(sanitizedUsername)
                                    .set(profileDetails)
                                    .addOnSuccessListener {
                                        // After success create sub-collection "ThePreferencesPlayer" and its document
                                        val preferencesPlayer = hashMapOf(
                                            "preferredPlayLocation" to "Location Not Yet Stated",
                                            "preferredTypeMatch" to "Not Yet Stated",
                                            "preferredHandPlay" to "Not Yet Stated",
                                            "preferredTimeToPlay" to "Not Yet Stated",
                                            "preferredCourtPosition" to "Not Yet Stated",
                                            "preferredGenderToPlayAgainst" to "Not Yet Stated"
                                        )
                                        db.collection("ThePlayers")
                                            .document(userId)
                                            .collection("ThePreferencesPlayer")
                                            .document("Preferences")
                                            .set(preferencesPlayer)
                                            .addOnSuccessListener {
                                                // Document creation successful
                                                showSnackbar("Registration successful!")
                                                startActivity(
                                                    Intent(
                                                        this,
                                                        LoginActivity::class.java
                                                    )
                                                )
                                                finish()
                                            }
                                            .addOnFailureListener { e ->
                                                // Handle document creation failure
                                                showSnackbar("Error creating preferences player: ${e.message}")
                                            }
                                    }
                                    .addOnFailureListener { e ->
                                        // Handle document creation failure
                                        showSnackbar("Error creating profile details: ${e.message}")
                                    }
                            }
                            .addOnFailureListener { e ->
                                // Handle player creation failure
                                showSnackbar("Error creating player: ${e.message}")
                            }
                    }
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
