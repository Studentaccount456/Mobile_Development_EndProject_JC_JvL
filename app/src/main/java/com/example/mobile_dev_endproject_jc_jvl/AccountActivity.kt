package com.example.mobile_dev_endproject_jc_jvl

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage;
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts

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
    private lateinit var logoutButton: Button
    private lateinit var pickImageLauncher: ActivityResultLauncher<Intent>

    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_account_screen)

        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottomNavigationView)

        bottomNavigationView.menu.findItem(R.id.navigation_account).isChecked = true

        bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_home -> {
                    launchActivity(HomeActivity::class.java)
                    true
                }
                R.id.navigation_court -> {
                    launchActivity(ClubActivity::class.java)
                    true
                }
                R.id.navigation_match -> {
                    launchActivity(MatchActivity::class.java)
                    true
                }
                R.id.navigation_account -> {
                    launchActivity(AccountActivity::class.java)

                    true
                }
                else -> false
            }
        }

        // Initialize views
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
        logoutButton = findViewById(R.id.logoutButton)

        // Fetch data from Firestore
        val userId = auth.currentUser?.uid
        if (userId != null) {
            val userRef = db.collection("ThePlayers").document(userId)
            userRef.get().addOnSuccessListener { document ->
                if (document != null && document.exists()) {
                    // Retrieve the "TheProfileDetails" sub-collection
                    val profileDetailsRef = userRef.collection("TheProfileDetails")
                    profileDetailsRef.get().addOnSuccessListener { profileDetailsSnapshot ->
                        for (profileDetailsDocument in profileDetailsSnapshot.documents) {
                            // Now you have access to each document in the "TheProfileDetails" sub-collection
                            val profileDetailsData = profileDetailsDocument.data

                            if (profileDetailsData != null) {
                                // Extract fields
                                val levelInformation = profileDetailsData["Level"]?.toString()?.toInt()
                                val followersInformation = profileDetailsData["Followers"]?.toString()?.toInt()
                                val followingInformation = profileDetailsData["Following"]?.toString()?.toInt()
                                val avatarUrl = profileDetailsData["Avatar"] as? String
                                val nickname = profileDetailsData["Nickname"] as? String

                                // Check if any of the required fields is null before updating UI
                                if (avatarUrl != null && nickname != null && levelInformation != null && followersInformation != null && followingInformation != null) {
                                    // Update UI with fetched data
                                    Glide.with(this).load(avatarUrl).into(profileImage)
                                    nicknameText.text = nickname
                                    followersText.text = "Followers: " + followersInformation.toString()
                                    followingText.text = "Following: " + followingInformation.toString()
                                    levelText.text = "Level: " + levelInformation.toString()


                                    // Fetch "ThePreferencesPlayer" sub-collection
                                    val preferencesRef = userRef.collection("ThePreferencesPlayer")
                                    preferencesRef.get().addOnSuccessListener { preferencesSnapshot ->
                                        for (preferencesDocument in preferencesSnapshot.documents) {
                                            // Now you have access to each document in the "ThePreferencesPlayer" sub-collection
                                            val preferencesData = preferencesDocument.data

                                            // Extract fields from the "ThePreferencesPlayer" document
                                            val typeMatch = preferencesData?.get("preferredTypeMatch") as? String
                                            val handPlay = preferencesData?.get("preferredHandPlay") as? String
                                            val timeToPlay = preferencesData?.get("preferredTimeToPlay") as? String
                                            val courtPosition = preferencesData?.get("preferredCourtPosition") as? String
                                            val genderToPlayAgainst =
                                                preferencesData?.get("preferredGenderToPlayAgainst") as? String
                                            val playLocation = preferencesData?.get("preferredPlayLocation") as? String

                                            // Check if any of the required preference fields is null before updating UI
                                            if (typeMatch != null && handPlay != null && timeToPlay != null
                                                && courtPosition != null && genderToPlayAgainst != null && playLocation != null
                                            ) {
                                                // Update UI with fetched preferences
                                                locationText.text = playLocation
                                                typeMatchText.text = "Type Match: " + typeMatch
                                                handPlayText.text = "Preferred Hand: " + handPlay
                                                timeToPlayText.text = "Preferred Time: " + timeToPlay
                                                courtPositionText.text = "Preferred Court Position: " + courtPosition
                                                genderToPlayAgainstText.text = "Preferred Gender to play against: " + genderToPlayAgainst
                                            } else {
                                                // Handle the case where some preference fields are null
                                                // Show an error message or take appropriate action
                                            }}}

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

                    // additional code
                }
            }
        }

        pickImageLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                val data: Intent? = result.data
                if (data != null && data.data != null) {
                    val imageUri: Uri = data.data!!
                    uploadImageToFirebaseStorage(imageUri)
                }
            }
        }

        profileImage.setOnClickListener {
            // Open the image picker or camera to choose a new avatar
            // You can use an external library like Intent.ACTION_PICK or Intent.ACTION_GET_CONTENT
            // or implement your own image picker logic
            openImagePicker()
        }


        // Set up the rest of your UI and handle button clicks as needed
        editProfileButton = findViewById(R.id.editProfileButton);

        editProfileButton.setOnClickListener {
            startActivity(Intent(this, EditProfileActivity::class.java))
        }

        logoutButton.setOnClickListener {
            auth.signOut()

            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)

            finish()
        }
    }

    private fun launchActivity(cls: Class<*>) {
        val intent = Intent(this, cls)
        startActivity(intent)
    }

    private fun openImagePicker() {
        val galleryIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        pickImageLauncher.launch(galleryIntent)
    }

    private fun uploadImageToFirebaseStorage(imageUri: Uri) {
        // Implement logic to upload the image to Firebase Storage
        // You can use the Firebase Storage API to upload the image
        // Example:
        val storageRef = FirebaseStorage.getInstance().reference
        val imageRef = storageRef.child("avatars/${auth.currentUser?.uid}.jpg")

        // Upload file to Firebase Storage
        imageRef.putFile(imageUri)
            .addOnSuccessListener { taskSnapshot ->
                // Image uploaded successfully
                // Get the download URL and update the user's profile
                imageRef.downloadUrl.addOnSuccessListener { uri ->
                    // Update the user's avatar URL in Firestore
                    updateAvatarUrl(uri.toString())
                }
            }
            .addOnFailureListener { e ->
                Log.e("AccountActivity", "Image upload failed: ${e.message}")
            }
    }

    private fun updateAvatarUrl(avatarUrl: String) {
        // Update the "Avatar" field in Firestore
        val userId = auth.currentUser?.uid
        if (userId != null) {
            val userRef = db.collection("ThePlayers").document(userId)
            val profileDetailsRef = userRef.collection("TheProfileDetails").document("Nickname")

            // Update the "Avatar" field with the new URL
            profileDetailsRef.update("Avatar", avatarUrl)
                .addOnSuccessListener {
                    // Avatar URL updated successfully
                    // Update the UI with the new avatar
                    Glide.with(this).load(avatarUrl).into(profileImage)
                }
                .addOnFailureListener { e ->
                    Log.e("AccountActivity", "Failed to update avatar URL: ${e.message}")
                }
        }
    }

    companion object {
        private const val PICK_IMAGE_REQUEST = 1
    }
}
