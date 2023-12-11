package com.example.mobile_dev_endproject_jc_jvl.activitiesDirectory

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mobile_dev_endproject_jc_jvl.R
import com.example.mobile_dev_endproject_jc_jvl.adaptersDirectory.CourtAdapter
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.firestore.FirebaseFirestore

class CourtListActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: CourtAdapter

    private lateinit var sanitizedClubName: String
    private lateinit var sanitizedClubEstablishment: String
    private lateinit var sentThroughClubName: String
    private lateinit var sentThroughClubEstablishment: String
    private lateinit var sentThroughEstablishmentAddress : String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.courtslist_screen)

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

        sentThroughEstablishmentAddress = intent.getStringExtra("sentThroughEstablishmentAddress").toString()

        // Retrieve sanitized club and establishment names from the intent
        sanitizedClubName = intent.getStringExtra("SanitizedClubName") ?: ""
        sanitizedClubEstablishment = intent.getStringExtra("SanitizedClubEstablishment") ?: ""
        sentThroughClubName = intent.getStringExtra("sentThroughClubName") ?: ""
        sentThroughClubEstablishment = intent.getStringExtra("sentThroughClubEstablishment") ?: ""
        Log.d("CourtListActivity", "1) Sent along?: $sanitizedClubName, $sanitizedClubEstablishment")


        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = CourtAdapter { sentThroughCourtName ->
            // Handle item click, and navigate to AccountActivity
            navigateToAccountActivity(sentThroughCourtName)
        }
        recyclerView.adapter = adapter

        // Fetch courts from Firebase Firestore
        fetchCourts()
    }

    private fun fetchCourts() {
        val db = FirebaseFirestore.getInstance()

        // Reference to the club's courts collection
        val courtsRef = db.collection("TheClubDetails")
            .document(sanitizedClubName)
            .collection("TheClubEstablishments")
            .document(sanitizedClubEstablishment)
            .collection("TheClubCourts")

        Log.d("CourtListActivity", "1.2) Sent along?: $sanitizedClubName, $sanitizedClubEstablishment")
        courtsRef.get()
            .addOnSuccessListener { documents ->
                Log.d("CourtListActivity", "2) Reaches here?: ${documents.documents}")
                // Clear existing data
                adapter.clearData()

                for (document in documents) {
                    val courtName = document.getString("CourtName") ?: ""
                    Log.d("CourtListActivity", "3) Fetch?: $courtName")
                    adapter.addData(courtName)
                }
            }
            .addOnFailureListener { exception ->
                Toast.makeText(this, "Error fetching courts: $exception", Toast.LENGTH_SHORT).show()
            }
    }




    private fun navigateToAccountActivity(sentThroughCourtName: String) {
        // Create an explicit intent to navigate to AccountActivity
        val sanitizedCourtName = sentThroughCourtName.replace("[\\s,\\\\/]".toRegex(), "")
        Log.d("CourtListActivity", "The right stuff sent? $sentThroughCourtName, $sanitizedCourtName")
        val intent = Intent(this, ReservationActivity::class.java).apply {
            putExtra("SanitizedClubName", sanitizedClubName)
            putExtra("SanitizedClubEstablishment", sanitizedClubEstablishment)
            putExtra("SanitizedCourtName", sanitizedCourtName)
            putExtra("sentThroughClubName", sentThroughClubName)
            putExtra("sentThroughClubEstablishment", sentThroughClubEstablishment)
            putExtra("sentThroughCourtName", sentThroughCourtName)
            putExtra("sentThroughEstablishmentAddress", sentThroughEstablishmentAddress)
        }
        startActivity(intent)
    }

    private fun launchActivity(cls: Class<*>) {
        val intent = Intent(this, cls)
        startActivity(intent)
    }
}
