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
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class JoinMatchActivity : AppCompatActivity() {

    private lateinit var dateReservationTextView: TextView
    private lateinit var timeslotTextView: TextView
    private lateinit var positionSquareTextView: TextView
    private lateinit var typeOfMatchTextview: TextView
    private lateinit var gendersAllowedTextview: TextView
    private lateinit var joinMatchButton: Button

    private var completedUpdates: Int = 0
    private var expectedUpdates: Int = 0

    private lateinit var matchId: String
    private lateinit var positionSquare: String

    private val userId: String
        get() = FirebaseAuth.getInstance().currentUser?.uid ?: ""

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.joinmatch_screen)

        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottomNavigationView)

        // Right Icon active
        bottomNavigationView.menu.findItem(R.id.navigation_match).isChecked = true

        bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_home -> {
                    launchActivity(HomeActivity::class.java)
                    true
                }

                R.id.navigation_establishment -> {
                    launchActivity(EstablishmentsActivity::class.java)
                    true
                }

                R.id.navigation_match -> {
                    launchActivity(MatchActivity::class.java)
                    true
                }

                R.id.navigation_account -> {
                    item.isChecked = true
                    launchActivity(AccountActivity::class.java)
                    true
                }

                else -> false
            }
        }

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
            joinMatch { launchActivity(MatchActivity::class.java) }
        }

        val returnButton: Button = findViewById(R.id.returnButton)
        returnButton.setOnClickListener {
            finish()
        }
    }

    private fun joinMatch(callback: () -> Unit) {


        // Counter to track the number of completed updates
        completedUpdates = 0

        // Variable to store the expected number of updates
        expectedUpdates = 3

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

                            // Update ThePlayerReservationsCourts subcollection
                            val reservationsRef =
                                userRef.collection("ThePlayerReservationsCourts").document(matchId)
                            val reservationsData = hashMapOf(
                                "matchId" to matchId
                            )

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
                                            completedUpdates++
                                            checkAndUpdateCompletion(callback)
                                            reservationsRef.set(reservationsData)
                                                .addOnSuccessListener {
                                                    Log.d("JoinMatchActivity", "Added matchId to ThePlayerReservationsCourts")
                                                    completedUpdates++
                                                    checkAndUpdateCompletion(callback)
                                                }
                                                .addOnFailureListener { e ->
                                                    // Handle failure
                                                }
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

                            val sentThroughClubName =
                                intent.getStringExtra("sentThroughClubName") ?: ""
                            val sentThroughClubEstablishmentName =
                                intent.getStringExtra("sentThroughClubEstablishmentName") ?: ""
                            val sentThroughCourtName =
                                intent.getStringExtra("sentThroughCourtName") ?: ""
                            val dateReservation = intent.getStringExtra("dateReservation")
                            val matchId = intent.getStringExtra("matchId")
                            positionSquare = intent.getStringExtra("PositionSquare") ?: ""


                            val db = FirebaseFirestore.getInstance()

                            // Reference to the document in the sub-collection
                            val documentReference =
                                db.collection("TheClubDetails/$sentThroughClubName/TheClubEstablishments/$sentThroughClubEstablishmentName/TheClubCourts")
                                    .document(sentThroughCourtName)

                            documentReference.get()
                                .addOnSuccessListener { documentSnapshot ->
                                    if (documentSnapshot.exists()) {
                                        // Get CourtReservations map
                                        val courtReservations =
                                            documentSnapshot.get("CourtReservations") as? Map<*, *>
                                        if (courtReservations != null) {
                                            // Get the specific array object based on dateReservation
                                            val reservationsArray =
                                                courtReservations[dateReservation] as? List<*>
                                            if (reservationsArray != null) {
                                                // Find the object with matching matchId
                                                val matchingReservation = reservationsArray.find {
                                                    (it as? Map<*, *>)?.get("MatchId") == matchId
                                                }

                                                if (matchingReservation != null) {
                                                    // Get the Participators map
                                                    val participators =
                                                        (matchingReservation as Map<*, *>)["Participators"] as? MutableMap<String, Any>

                                                    // Update values based on positionSquare
                                                    participators?.let {
                                                        it["UserName_$positionSquare"] = username
                                                        it["UserAvatar_$positionSquare"] = avatar
                                                        it["UserId_$positionSquare"] = userId

                                                        // Update the Participators map in Firestore
                                                        documentReference.update(
                                                            "CourtReservations.$dateReservation",
                                                            reservationsArray
                                                        )
                                                            .addOnSuccessListener {
                                                                completedUpdates++
                                                                checkAndUpdateCompletion(callback)
                                                            }
                                                            .addOnFailureListener { e ->
                                                                // Handle failure
                                                            }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                                .addOnFailureListener { e ->
                                    // Handle failure
                                }
                        }
                    }
                }
            }
        }
    }

    private fun launchActivity(cls: Class<*>) {
        val intent = Intent(this, cls)
        startActivity(intent)
    }

    private fun sanitizeUsername(username: String): String {
        // Implement your logic to sanitize the username (remove spaces or special characters)
        // For example, you can replace spaces with underscores
        return username.replace("\\s+".toRegex(), "")
    }


    // Function to check and update the completion status
    private fun checkAndUpdateCompletion(callback: () -> Unit) {
        Log.d(
            "ReservationActivity",
            "CompletedUpdates: $completedUpdates | CompletedUpdates: $expectedUpdates"
        )
        if (completedUpdates == expectedUpdates) {
            callback()
        }
    }
}




