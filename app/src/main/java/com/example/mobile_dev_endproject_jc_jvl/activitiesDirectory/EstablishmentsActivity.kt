package com.example.mobile_dev_endproject_jc_jvl.activitiesDirectory

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mobile_dev_endproject_jc_jvl.adaptersDirectory.ClubEstablishmentAdapter
import com.example.mobile_dev_endproject_jc_jvl.dataClassesDirectory.ClubEstablishment
import com.example.mobile_dev_endproject_jc_jvl.R
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.tabs.TabLayout
import com.google.firebase.firestore.FirebaseFirestore


class EstablishmentsActivity : AppCompatActivity() {

    private val db = FirebaseFirestore.getInstance()
    private val clubEstablishments = mutableListOf<ClubEstablishment>()
    private lateinit var adapter: ClubEstablishmentAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.establishments_screen)
        val tabLayout: TabLayout = findViewById(R.id.tabLayout_Establishments)

        // Add tabs with titles
        val establishmentsTab = tabLayout.newTab().setText("Establishments")
        val reservationsTab = tabLayout.newTab().setText("Your Courts Reservations")
        tabLayout.addTab(establishmentsTab)
        tabLayout.addTab(reservationsTab)

        // Set up a tab selected listener
        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                when (tab.position) {
                    0 -> {
                        // Start EstablishmentsActivity
                        //launchActivity(ClubEstablishmentsActivity::class.java)

                    }
                    1 -> {
                        // Start YourCourtReservationsActivity
                        launchActivity(YourCourtReservationsActivity::class.java)
                    }
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab) {
                // Handle tab unselection if needed
            }

            override fun onTabReselected(tab: TabLayout.Tab) {
                // Handle tab reselection if needed
            }
        })

        // Select the tab you want (e.g., "Your Courts Reservations")
        establishmentsTab.select()

        val recyclerView: RecyclerView = findViewById(R.id.recyclerViewClubEstablishments)
        recyclerView.layoutManager = LinearLayoutManager(this)

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

                    // Retrieve data from the sub-collection
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
            putExtra("EstablishmentName", clubEstablishment.clubEstablishmentName)
        }
        startActivity(intent)
    }

    private fun launchActivity(cls: Class<*>) {
        val intent = Intent(this, cls)
        startActivity(intent)
    }
}