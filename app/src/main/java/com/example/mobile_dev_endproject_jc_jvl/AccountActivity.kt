package com.example.mobile_dev_endproject_jc_jvl

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlin.math.log

class AccountActivity : AppCompatActivity(){

    private lateinit var profileImage: ImageView
    private lateinit var nicknameText: TextView
    private lateinit var locationText: TextView
    private lateinit var followersText: TextView
    private lateinit var followingText: TextView
    private lateinit var levelText: TextView
    private lateinit var editProfileButton: Button
    private lateinit var preferencesTitle: TextView
    private lateinit var typeMatchText: TextView
    private lateinit var handPlayText: TextView
    private lateinit var timeToPlayText: TextView
    private lateinit var courtPositionText: TextView
    private lateinit var genderToPlayAgainstText: TextView

    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_account_screen)

        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottomNavigationView)

        // Right Icon active
        bottomNavigationView.menu.findItem(R.id.navigation_account).isChecked = true

        bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_home -> {
                    launchActivity(HomeActivity::class.java)
                    true
                }
                R.id.navigation_account -> {
                    launchActivity(AccountActivity::class.java)

                    true
                }
                else -> false
            }
        }


        // Initialize your views
        profileImage = findViewById(R.id.profileImage)
        nicknameText = findViewById(R.id.nicknameText)
        locationText = findViewById(R.id.locationText)
        followersText = findViewById(R.id.followersText)
        followingText = findViewById(R.id.followingText)
        levelText = findViewById(R.id.levelText)
        editProfileButton = findViewById(R.id.editProfileButton)
        preferencesTitle = findViewById(R.id.preferencesTitle)
        typeMatchText = findViewById(R.id.typeMatchText)
        handPlayText = findViewById(R.id.handPlayText)
        timeToPlayText = findViewById(R.id.timeToPlayText)
        courtPositionText = findViewById(R.id.courtPositionText)
        genderToPlayAgainstText = findViewById(R.id.genderToPlayAgainstText)

        // Fetch data from Firestore
        val userId = auth.currentUser?.uid
        if (userId != null) {
            val userRef = db.collection("ThePlayers").document(userId)
            userRef.get().addOnSuccessListener { document ->
                if (document != null && document.exists()) {
                    Log.d("AccountActivity", "1) Document data: ${document.data}")

                    // Retrieve the "TheProfileDetails" subcollection
                    val profileDetailsRef = userRef.collection("TheProfileDetails")
                    profileDetailsRef.get().addOnSuccessListener { profileDetailsSnapshot ->
                        for (profileDetailsDocument in profileDetailsSnapshot.documents) {
                            // Now you have access to each document in the "TheProfileDetails" subcollection
                            val profileDetailsData = profileDetailsDocument.data
                            Log.d("AccountActivity", "2) Profile Details data: $profileDetailsData")

                            if (profileDetailsData != null) {
                                // Extract fields
                                val levelInformation = profileDetailsData["Level"]?.toString()?.toInt()
                                Log.d("AccountActivity", "2.1) Profile Details data: $levelInformation")
                                val followersInformation = profileDetailsData["Followers"]?.toString()?.toInt()
                                Log.d("AccountActivity", "2.2) Profile Details data: $followersInformation")
                                val followingInformation = profileDetailsData["Following"]?.toString()?.toInt()
                                Log.d("AccountActivity", "2.3) Profile Details data: $followingInformation")
                                val avatarUrl = profileDetailsData["Avatar"] as? String
                                val nickname = profileDetailsData["Nickname"] as? String
                                Log.d("AccountActivity", "2.0) Profile Details data: $nickname")
                                Log.d(
                                    "AccountActivity",
                                    "Avatar URL: $avatarUrl, Nickname: $nickname, Level: $levelInformation, followers: $followersInformation, following: $followingInformation "
                                )

                                // Check if any of the required fields is null before updating UI
                                if (avatarUrl != null && nickname != null && levelInformation != null && followersInformation != null && followingInformation != null) {
                                    // Update UI with fetched data
                                    Glide.with(this).load(avatarUrl).into(profileImage)
                                    nicknameText.text = nickname
                                    followersText.text = "Followers: " + followersInformation.toString()
                                    followingText.text = "Following: " + followingInformation.toString()
                                    levelText.text = "Level: " + levelInformation.toString()


                                    // Fetch "ThePreferencesPlayer" subcollection
                                    val preferencesRef = userRef.collection("ThePreferencesPlayer")
                                    preferencesRef.get().addOnSuccessListener { preferencesSnapshot ->
                                        for (preferencesDocument in preferencesSnapshot.documents) {
                                            // Now you have access to each document in the "ThePreferencesPlayer" subcollection
                                            val preferencesData = preferencesDocument.data
                                            Log.d("AccountActivity", "3) Preferences data: $preferencesData")

                                            // Extract fields from the "ThePreferencesPlayer" document
                                            val typeMatch = preferencesData?.get("PrefferedTypeMatch") as? String
                                            val handPlay = preferencesData?.get("PrefferedHandPlay") as? String
                                            val timeToPlay = preferencesData?.get("PrefferedTimeToPlay") as? String
                                            val courtPosition = preferencesData?.get("PrefferedCourtPosition") as? String
                                            val genderToPlayAgainst =
                                                preferencesData?.get("PrefferedGenderToPlayAgainst") as? String
                                            val playLocation = preferencesData?.get("PrefferedPlayLocation") as? String

                                            // Check if any of the required preference fields is null before updating UI
                                            if (typeMatch != null && handPlay != null && timeToPlay != null
                                                && courtPosition != null && genderToPlayAgainst != null && playLocation != null
                                            ) {
                                                // Update UI with fetched preferences
                                                locationText.text = playLocation
                                                typeMatchText.text = "Type Match: " + typeMatch
                                                handPlayText.text = "Preferred Hand: " + handPlay
                                                timeToPlayText.text = "Preffered Time: " + timeToPlay
                                                courtPositionText.text = "Preffered Court Position: " + courtPosition
                                                genderToPlayAgainstText.text = "Preffered Gender to play against: " + genderToPlayAgainst
                                            } else {
                                                // Handle the case where some preference fields are null
                                                // Show an error message or take appropriate action
                                            }
                                        }
                                    }
                                } else {
                                    // Handle the case where some fields in "Nickname" document are null
                                    // Show an error message or take appropriate action
                                }
                            } else {
                                // Handle the case where "Nickname" document is null
                                // Show an error message or take appropriate action
                            }
                        }
                    }

                    // Continue with the rest of your code...
                }
            }
        }


        // Set up the rest of your UI and handle button clicks as needed
            }

    private fun launchActivity(cls: Class<*>) {
        val intent = Intent(this, cls)
        startActivity(intent)
    }
        }