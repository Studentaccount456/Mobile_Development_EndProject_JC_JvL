package com.example.mobile_dev_endproject_jc_jvl.ActivitiesDirectory

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
import org.osmdroid.util.GeoPoint

class EstablishmentDetailsActivity : AppCompatActivity() {

    private lateinit var firestore: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.establishmentdetails_screen)

        firestore = FirebaseFirestore.getInstance()

        val intent = intent
        val clubName = intent.getStringExtra("ClubName")
        val courtAddress = intent.getStringExtra("ClubEstablishmentAddress")
        val establishmentName = intent.getStringExtra("EstablishmentName")

        findViewById<TextView>(R.id.textViewCourtAddress).text = courtAddress
        findViewById<TextView>(R.id.textViewClubEstablishmentName).text = establishmentName

        fetchClubData(clubName)
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
                // Handle errors
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
        // Handle reserve button click
    }

    fun onReturnClicked(view: View) {
        val receivedCoordinates = intent.getParcelableExtra<Parcelable>("TheMapCoordinates") as? GeoPoint

        if (receivedCoordinates != null) {
            // "TheMapCoordinates" received, send it back to MapActivity
            val mapIntent = Intent(this, MapActivity::class.java)
            mapIntent.putExtra("TheMapCoordinates", receivedCoordinates as Parcelable)
            startActivity(mapIntent)
        } else {
            // "TheMapCoordinates" not received, go to ClubEstablishmentsActivity
            val clubIntent = Intent(this, EstablishmentsActivity::class.java)
            startActivity(clubIntent)
        }
    }
}
