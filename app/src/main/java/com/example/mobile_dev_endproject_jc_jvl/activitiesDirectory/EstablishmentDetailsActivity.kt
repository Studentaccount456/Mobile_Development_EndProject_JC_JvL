package com.example.mobile_dev_endproject_jc_jvl.activitiesDirectory

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import android.os.Parcelable
import com.example.mobile_dev_endproject_jc_jvl.R
import com.google.android.material.bottomnavigation.BottomNavigationView
import org.osmdroid.util.GeoPoint

class EstablishmentDetailsActivity : AppCompatActivity() {

    private lateinit var firestore: FirebaseFirestore
    private var sanitizedClubName: String? = null
    private var sanitizedEstablishmentName: String? = null
    private lateinit var sentThroughClubName: String
    private lateinit var sentThroughClubEstablishment: String
    private lateinit var sentThroughEstablishmentAddress: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.establishmentdetails_screen)

        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottomNavigationView)

        // Right Icon active
        bottomNavigationView.menu.findItem(R.id.navigation_establishment).isChecked = true

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

        sentThroughEstablishmentAddress =
            intent.getStringExtra("ClubEstablishmentAddress").toString()

        firestore = FirebaseFirestore.getInstance()

        val intent = intent
        sentThroughClubName = intent.getStringExtra("ClubName").toString()
        if (sentThroughClubName != null) {
            sanitizedClubName = sentThroughClubName.replace("[\\s,\\\\/]".toRegex(), "")
        }
        val courtAddress = intent.getStringExtra("ClubEstablishmentAddress")
        sentThroughClubEstablishment = intent.getStringExtra("EstablishmentName").toString()
        if (sentThroughClubEstablishment != null) {
            sanitizedEstablishmentName =
                sentThroughClubEstablishment.replace("[\\s,\\\\/]".toRegex(), "")
        }

        findViewById<TextView>(R.id.textViewCourtAddress).text = courtAddress
        findViewById<TextView>(R.id.textViewClubEstablishmentName).text =
            sentThroughClubEstablishment

        fetchClubData(sentThroughClubName)
    }

    private fun fetchClubData(clubName: String?) {
        if (clubName == null) {
            // Handle the case where clubName is null
            return
        }

        val clubDocumentRef = firestore.collection("TheClubDetails").document(clubName)

        clubDocumentRef.get()
            .addOnSuccessListener { documentSnapshot: DocumentSnapshot? ->
                if (documentSnapshot != null && documentSnapshot.exists()) {
                    // DocumentSnapshot exists, extract data
                    val clubNameWithSpaces = documentSnapshot.getString("ClubName")
                    val clubDescription = documentSnapshot.getString("ClubDescription")
                    val imageURL = documentSnapshot.getString("ClubLogo")

                    // Set data to the corresponding TextViews
                    findViewById<TextView>(R.id.textViewClubName).text = clubNameWithSpaces
                    findViewById<TextView>(R.id.textViewClubDescription).text = clubDescription
                    loadClubLogo(imageURL)
                } else {
                    // Handle the case where the document doesn't exist
                }
            }
            .addOnFailureListener { exception: Exception ->
                Log.e(
                    "EstablishmentDetailsActivity",
                    "Exception occurred: ${exception.message}",
                    exception
                )
            }
    }

    private fun loadClubLogo(imageURL: String?) {
        if (imageURL != null) {
            Log.d("EstablishmentDetailsActivity", "Image URL: $imageURL")
            // Load club logo using an image loading library like Picasso or Glide
            val imageViewClubLogo = findViewById<ImageView>(R.id.imageViewClubLogo)
            Glide.with(this)
                .load(imageURL)
                .into(imageViewClubLogo)
        }
    }

    fun onReserveClicked(view: View) {
        val mapIntent = Intent(this, CourtListActivity::class.java)
        mapIntent.putExtra("SanitizedClubName", sanitizedClubName)
        mapIntent.putExtra("SanitizedClubEstablishment", sanitizedEstablishmentName)
        mapIntent.putExtra("sentThroughClubName", sentThroughClubName)
        mapIntent.putExtra("sentThroughClubEstablishment", sentThroughClubEstablishment)
        mapIntent.putExtra("sentThroughEstablishmentAddress", sentThroughEstablishmentAddress)
        startActivity(mapIntent)
    }

    // .xml relies on view!!!!
    fun onReturnClicked(view: View) {
        val receivedCoordinates =
            intent.getParcelableExtra<Parcelable>("TheMapCoordinates") as? GeoPoint

        if (receivedCoordinates != null) {
            val mapIntent = Intent(this, MapActivity::class.java)
            mapIntent.putExtra("TheMapCoordinates", receivedCoordinates as Parcelable)
            startActivity(mapIntent)
        } else {
            val clubIntent = Intent(this, EstablishmentsActivity::class.java)
            startActivity(clubIntent)
        }
    }

    private fun launchActivity(cls: Class<*>) {
        val intent = Intent(this, cls)
        startActivity(intent)
    }
}
