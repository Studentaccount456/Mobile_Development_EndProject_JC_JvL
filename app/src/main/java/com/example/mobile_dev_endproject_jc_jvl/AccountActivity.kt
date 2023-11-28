package com.example.mobile_dev_endproject_jc_jvl

import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

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
                    // Get profile details
                    val profileDetails = document.get("TheProfileDetails") as Map<String, Any>
                    val avatarUrl = profileDetails["Avatar"] as String
                    val nickname = profileDetails["Nickname"] as String
                    val playLocation = profileDetails["PrefferedPlayLocation"] as String

                    // Get preferences
                    val preferences =
                        document.get("ThePreferencesPlayer") as Map<String, Any>
                    val typeMatch = preferences["PrefferedTypeMatch"] as String
                    val handPlay = preferences["PrefferedHandPlay"] as String
                    val timeToPlay = preferences["PrefferedTimeToPlay"] as String
                    val courtPosition = preferences["PrefferedCourtPosition"] as String
                    val genderToPlayAgainst = preferences["PrefferedGenderToPlayAgainst"] as String

                    // Update UI with fetched data
                    Glide.with(this).load(avatarUrl).into(profileImage)
                    nicknameText.text = nickname
                    locationText.text = playLocation
                    typeMatchText.text = typeMatch
                    handPlayText.text = handPlay
                    timeToPlayText.text = timeToPlay
                    courtPositionText.text = courtPosition
                    genderToPlayAgainstText.text = genderToPlayAgainst
                }
            }
        }

        // Set up the rest of your UI and handle button clicks as needed
    }
}