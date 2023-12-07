package com.example.mobile_dev_endproject_jc_jvl

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.firestore.FirebaseFirestore

class ClubEstablishmentsActivity : AppCompatActivity() {

    private val db = FirebaseFirestore.getInstance()
    private val clubEstablishments = mutableListOf<ClubEstablishment>()
    private lateinit var adapter: ClubEstablishmentAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.club_establishments_screen)

        val recyclerView: RecyclerView = findViewById(R.id.recyclerViewClubEstablishments)
        recyclerView.layoutManager = LinearLayoutManager(this)

        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottomNavigationView)

        // Right Icon active
        bottomNavigationView.menu.findItem(R.id.navigation_court).isChecked = true

        bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_home -> {
                    launchActivity(HomeActivity::class.java)
                    true
                }
                R.id.navigation_court -> {
                    launchActivity(ClubEstablishmentsActivity::class.java)
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

        // Initialize the adapter
        adapter = ClubEstablishmentAdapter(clubEstablishments) { clubEstablishment ->
            onClubEstablishmentClicked(clubEstablishment)
        }

        recyclerView.adapter = adapter

        // Replace this with the actual path to your Firestore collection
        val collectionPath = "TheClubDetails"

// Retrieve data from Firestore
        db.collection(collectionPath)
            .get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    // Extract data and add to the list
                    val clubNameDocumentId = document.id
                    val establishmentsCollectionPath = "$collectionPath/$clubNameDocumentId/TheClubEstablishments"

                    // Retrieve data from the subcollection
                    db.collection(establishmentsCollectionPath)
                        .get()
                        .addOnSuccessListener { establishmentDocuments ->
                            for (establishmentDocument in establishmentDocuments) {
                                val establishmentName = establishmentDocument.getString("ClubEstablishmentName") ?: ""
                                val establishmentAddress = establishmentDocument.getString("ClubEstablishmentAddress") ?: ""

                                Log.d("Firestore", "Fetched document - ClubName: $clubNameDocumentId, EstablishmentName: $establishmentName, EstablishmentAddress: $establishmentAddress")

                                val clubEstablishment = ClubEstablishment(clubNameDocumentId, establishmentName, establishmentAddress)
                                clubEstablishments.add(clubEstablishment)

                                // Add log statement to check which documents are being fetched
                                Log.d("Firestore", "Fetched document - ClubName: $clubNameDocumentId, EstablishmentName: $establishmentName, EstablishmentAddress: $establishmentAddress")
                            }

                            // Notify the adapter that the data set has changed
                            adapter.notifyDataSetChanged()
                        }
                        .addOnFailureListener { exception ->
                            // Handle failures here
                            Log.e("Firestore", "Error fetching establishment documents", exception)
                        }
                }
            }
            .addOnFailureListener { exception ->
                // Handle failures here
                Log.e("Firestore", "Error fetching documents", exception)
            }
    }

    private fun onClubEstablishmentClicked(clubEstablishment: ClubEstablishment) {
        val intent = Intent(this, EstablishmentDetailsActivity::class.java).apply {
            putExtra("ClubName", clubEstablishment.clubName)
            putExtra("ClubEstablishmentAddress", clubEstablishment.clubEstablishmentAddress)
        }
        startActivity(intent)
    }

    private fun launchActivity(cls: Class<*>) {
        val intent = Intent(this, cls)
        startActivity(intent)
    }
}