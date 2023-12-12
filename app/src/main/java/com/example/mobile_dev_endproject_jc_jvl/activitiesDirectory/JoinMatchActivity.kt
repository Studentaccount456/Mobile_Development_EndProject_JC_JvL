package com.example.mobile_dev_endproject_jc_jvl.activitiesDirectory

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.mobile_dev_endproject_jc_jvl.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class JoinMatchActivity : AppCompatActivity() {

    private lateinit var dateReservationTextView: TextView
    private lateinit var timeslotTextView: TextView
    private lateinit var positionSquareTextView: TextView
    private lateinit var typeOfMatchTextview: TextView
    private lateinit var gendersAllowedTextview: TextView
    private lateinit var joinMatchButton: Button

    private lateinit var matchId: String
    private lateinit var positionSquare: String

    private val userId: String
        get() = FirebaseAuth.getInstance().currentUser?.uid ?: ""

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.joinmatch_screen)

        dateReservationTextView = findViewById(R.id.dateReservationTextView)
        timeslotTextView = findViewById(R.id.timeslotTextView)
        positionSquareTextView = findViewById(R.id.positionSquareTextView)
        typeOfMatchTextview = findViewById(R.id.typeOfMatchTextview)
        gendersAllowedTextview = findViewById(R.id.gendersAllowedTextview)
        joinMatchButton = findViewById(R.id.joinMatchButton)

        val intent = intent
        matchId = intent.getStringExtra("matchId") ?: ""
        positionSquare = intent.getStringExtra("PositionSquare") ?: ""
        Log.d("JoinMatchActivity", "Data $positionSquare")

        // Set values from intent
        dateReservationTextView.text = "Date Match: ${intent.getStringExtra("dateReservation")}"
        timeslotTextView.text = "Time Match: ${intent.getStringExtra("timeslot")}"
        positionSquareTextView.text = "Place you join: $positionSquare"
        typeOfMatchTextview.text = "Type of match: ${intent.getStringExtra("typeOfMatch")}"
        gendersAllowedTextview.text =
            "Genders allowed in match: ${intent.getStringExtra("gendersAllowed")}"

        joinMatchButton.setOnClickListener {
            joinMatch()
        }

        val returnButton: Button = findViewById(R.id.returnButton)
        returnButton.setOnClickListener {
            finish()
        }
    }

    private fun joinMatch() {
        val auth = FirebaseAuth.getInstance()
        val userId = auth.currentUser?.uid

        if (userId != null) {
            val db = FirebaseFirestore.getInstance()
            val matchRef = db.collection("TheMatches").document(matchId)

            // Fetch current user details
            val userRef = db.collection("ThePlayers").document(userId)
            userRef.get().addOnSuccessListener { userSnapshot ->
                if (userSnapshot.exists()) {
                    val username = userSnapshot.getString("username") ?: ""
                    val sanitizedUsername = sanitizeUsername(username)
                    Log.d(
                        "JoinMatchActivity",
                        "1) username $username, sanitizedUsername, $sanitizedUsername"
                    )


                    // Fetch user profile details
                    val profileRef =
                        userRef.collection("TheProfileDetails").document(sanitizedUsername)
                    profileRef.get().addOnSuccessListener { profileSnapshot ->
                        if (profileSnapshot.exists()) {
                            val avatar = profileSnapshot.getString("Avatar") ?: ""
                            Log.d("JoinMatchActivity", " 2) avatar $avatar")

                            matchRef.get().addOnSuccessListener { documentSnapshot ->
                                Log.d(
                                    "JoinMatchActivity",
                                    " 2.1) document ${documentSnapshot.data}"
                                )
                                if (documentSnapshot.exists()) {
                                    val participators =
                                        documentSnapshot["participators"] as Map<*, *>?
                                    Log.d("JoinMatchActivity", " 3) participators $participators")

                                    // Check if the user is already in the match
                                    if (participators == null || participators.values.none { it == userId }) {
                                        Log.d("JoinMatchActivity", " 4) Triggers?")
                                        // Update the participator map with the user's information
                                        val updatedParticipators =
                                            participators?.toMutableMap() ?: mutableMapOf()
                                        val userKey =
                                            "UserName_$positionSquare"
                                        val avatarKey =
                                            "UserAvatar_$positionSquare"
                                        val userIdKey =
                                            "UserId_$positionSquare"

                                        updatedParticipators[userKey] = username
                                        updatedParticipators[avatarKey] = avatar
                                        updatedParticipators[userIdKey] = userId

                                        // Check and update only if the fields have the value "Default"
                                        var hasDefaultValues = false
                                        for ((key, value) in updatedParticipators) {
                                            if (value == "Default") {
                                                hasDefaultValues = true
                                                // Set the actual values from the user's details
                                                updatedParticipators[userKey] = username
                                                updatedParticipators[avatarKey] = avatar
                                                updatedParticipators[userIdKey] = userId
                                            }
                                        }

                                        // Update the document with the new participators if there are "Default" values
                                        if (hasDefaultValues) {
                                            matchRef.update("participators", updatedParticipators)
                                            Toast.makeText(
                                                this,
                                                "Joined the match!",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        } else {
                                            Toast.makeText(
                                                this,
                                                "Another player already joined!",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        }
                                    } else {
                                        Toast.makeText(
                                            this,
                                            "You are already in this match",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private fun sanitizeUsername(username: String): String {
        // Implement your logic to sanitize the username (remove spaces or special characters)
        // For example, you can replace spaces with underscores
        return username.replace("\\s+".toRegex(), "")
    }
}




