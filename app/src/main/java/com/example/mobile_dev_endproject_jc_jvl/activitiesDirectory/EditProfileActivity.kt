package com.example.mobile_dev_endproject_jc_jvl.activitiesDirectory

import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.example.mobile_dev_endproject_jc_jvl.dataClassesDirectory.Preferences
import com.example.mobile_dev_endproject_jc_jvl.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class EditProfileActivity : AppCompatActivity() {

    private lateinit var playLocationEditText: EditText
    private lateinit var typeMatchSpinner: Spinner
    private lateinit var handPlaySpinner: Spinner
    private lateinit var timeToPlaySpinner: Spinner
    private lateinit var courtPositionSpinner: Spinner
    private lateinit var genderSpinner: Spinner

    private lateinit var saveButton: Button
    private lateinit var returnButton: Button

    private lateinit var userId: String
    private lateinit var firestore: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.edit_profile)

        // Initialize UI components
        playLocationEditText = findViewById(R.id.playLocationEditText)
        typeMatchSpinner = findViewById(R.id.typeMatchSpinner)
        handPlaySpinner = findViewById(R.id.handPlaySpinner)
        timeToPlaySpinner = findViewById(R.id.timeToPlaySpinner)
        courtPositionSpinner = findViewById(R.id.courtPositionSpinner)
        genderSpinner = findViewById(R.id.genderSpinner)
        saveButton = findViewById(R.id.saveButton)
        returnButton = findViewById(R.id.returnButton)

        // Initialize Firestore and current user ID
        firestore = FirebaseFirestore.getInstance()
        userId = FirebaseAuth.getInstance().currentUser?.uid ?: ""

        // Populate UI with Firestore data
        populateUIFromFirestore()

        // Set onClickListener for buttons
        returnButton.setOnClickListener {
            startActivity(Intent(this, AccountActivity::class.java))
        }

        saveButton.setOnClickListener {
            saveProfileDataToFirestore()
        }
    }

    private fun populateUIFromFirestore() {
        // Retrieve user preferences from Firestore and populate UI
        firestore.collection("ThePlayers")
            .document(userId)
            .collection("ThePreferencesPlayer")
            .document("UserId")
            .get()
            .addOnSuccessListener { documentSnapshot ->
                val preferences = documentSnapshot.toObject(Preferences::class.java)

                if (documentSnapshot.exists() && preferences != null) {
                    // Update fields with non-default values
                    val playLocation = preferences.preferredPlayLocation
                    if (playLocation != "Location Not Yet Stated") {
                        playLocationEditText.setText(preferences.preferredPlayLocation)
                    }
                    //playLocationEditText.setText(preferences.preferredPlayLocation)
                    typeMatchSpinner.setSelection(
                        getIndex(
                            typeMatchSpinner,
                            preferences.preferredTypeMatch
                        )
                    )
                    handPlaySpinner.setSelection(
                        getIndex(
                            handPlaySpinner,
                            preferences.preferredHandPlay
                        )
                    )
                    timeToPlaySpinner.setSelection(
                        getIndex(
                            timeToPlaySpinner,
                            preferences.preferredTimeToPlay
                        )
                    )
                    courtPositionSpinner.setSelection(
                        getIndex(
                            courtPositionSpinner,
                            preferences.preferredCourtPosition
                        )
                    )
                    genderSpinner.setSelection(
                        getIndex(
                            genderSpinner,
                            preferences.preferredGenderToPlayAgainst
                        )
                    )
                } else {
                    // Error handling
                }
            }
    }

    private fun saveProfileDataToFirestore() {
        // Get selected values from UI
        val preferredPlayLocation = playLocationEditText.text.toString().trim()
        val preferredTypeMatch = typeMatchSpinner.selectedItem.toString()
        val preferredHandPlay = handPlaySpinner.selectedItem.toString()
        val preferredTimeToPlay = timeToPlaySpinner.selectedItem.toString()
        val preferredCourtPosition = courtPositionSpinner.selectedItem.toString()
        val preferredGenderToPlayAgainst = genderSpinner.selectedItem.toString()

        // Check if playLocationEditText is empty
        if (preferredPlayLocation.isEmpty()) {
            playLocationEditText.error = "Please fill in the location"
            return
        }

        // Update Firestore with new values
        val preferences = Preferences(
            preferredPlayLocation,
            preferredTypeMatch,
            preferredHandPlay,
            preferredTimeToPlay,
            preferredCourtPosition,
            preferredGenderToPlayAgainst
        )

        firestore.collection("ThePlayers")
            .document(userId)
            .collection("ThePreferencesPlayer")
            .document("UserId")
            .set(preferences)
            .addOnSuccessListener {
                // Data saved successfully
                // You can add any additional logic here
                setResult(RESULT_OK)
                startActivity(Intent(this, AccountActivity::class.java))
            }
            .addOnFailureListener {
                Toast.makeText(this, "Failed to save data", Toast.LENGTH_SHORT).show()
            }
    }

    private fun getIndex(spinner: Spinner, value: String): Int {
        for (i in 0 until spinner.count) {
            if (spinner.getItemAtPosition(i).toString() == value) {
                return i
            }
        }
        return 0 // Default to the first item if not found
    }
}
