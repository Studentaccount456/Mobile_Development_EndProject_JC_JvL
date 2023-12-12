package com.example.mobile_dev_endproject_jc_jvl.activitiesDirectory

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mobile_dev_endproject_jc_jvl.R
import com.example.mobile_dev_endproject_jc_jvl.adaptersDirectory.CourtReservationAdapter
import com.example.mobile_dev_endproject_jc_jvl.dataClassesDirectory.CourtReservation
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.tabs.TabLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore


class YourCourtReservationsActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var courtReservationAdapter: CourtReservationAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.yourreservationscourts_screen)
        val tabLayout: TabLayout = findViewById(R.id.tabLayout_reservationCourt)

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
                        launchActivity(EstablishmentsActivity::class.java)
                    }

                    1 -> {
                        // Start YourCourtReservationsActivity
                        //launchActivity(YourCourtReservationsActivity::class.java)
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
        reservationsTab.select()

        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)

        val userId = FirebaseAuth.getInstance().currentUser?.uid
        val query = FirebaseFirestore.getInstance()
            .collection("ThePlayers")
            .document(userId!!)
            .collection("ThePlayerReservationsCourts")

        query.get().addOnSuccessListener { documents ->
            val courtReservations = mutableListOf<CourtReservation>()

            for (document in documents) {
                val courtReservation = CourtReservation(
                    document.getString("clubEstablishmentName") ?: "",
                    document.getString("clubEstablishmentAddress") ?: "",
                    document.getString("courtName") ?: "",
                    document.getString("dateReservation") ?: "",
                    document.getString("timeslot") ?: ""
                )
                courtReservations.add(courtReservation)
            }

            courtReservationAdapter = CourtReservationAdapter(courtReservations)
            recyclerView.adapter = courtReservationAdapter
        }.addOnFailureListener { exception ->
            // Handle failure
        }

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
    }

    private fun launchActivity(cls: Class<*>) {
        val intent = Intent(this, cls)
        startActivity(intent)
    }
}