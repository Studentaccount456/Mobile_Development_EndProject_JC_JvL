package com.example.mobile_dev_endproject_jc_jvl.activitiesDirectory

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mobile_dev_endproject_jc_jvl.R
import com.example.mobile_dev_endproject_jc_jvl.adaptersDirectory.MatchAdapter
import com.example.mobile_dev_endproject_jc_jvl.dataClassesDirectory.Match
import com.google.android.material.tabs.TabLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FieldPath

class YourMatchesActivity : AppCompatActivity() {

    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
    private lateinit var recyclerView: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.yourmatches_screen)

        val tabLayout: TabLayout = findViewById(R.id.tabLayout_Establishments)

        // Add tabs with titles
        val allMatchesTab = tabLayout.newTab().setText("All Matches")
        val yourMatchesTab = tabLayout.newTab().setText("Your Matches")
        tabLayout.addTab(allMatchesTab)
        tabLayout.addTab(yourMatchesTab)

        // Set up a tab selected listener
        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                when (tab.position) {
                    0 -> {
                        // Start EstablishmentsActivity
                        launchActivity(MatchActivity::class.java)

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
        yourMatchesTab.select()

        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)

        fetchDataFromFirestore()
    }

    private fun fetchDataFromFirestore() {
        val currentUser = FirebaseAuth.getInstance().currentUser
        val userId = currentUser?.uid

        userId?.let { uid ->
            val playersCollection = firestore.collection("ThePlayers").document(uid)
            val reservationsCollection = playersCollection.collection("ThePlayerReservationsCourts")

            reservationsCollection.get()
                .addOnSuccessListener { reservationSnapshot ->
                    Log.d("YourMatchesActivity", " 1) data ${reservationSnapshot.documents}")
                    val matchIds = mutableListOf<String>()
                    Log.d("YourMatchesActivity", " 2) data $matchIds")

                    for (reservationDocument in reservationSnapshot.documents) {
                        Log.d("YourMatchesActivity", " 3) data $reservationDocument")
                        val matchId = reservationDocument.getString("matchId")
                        matchId?.let {
                            if (it.isNotEmpty()) { // Check if MatchId is not empty
                                matchIds.add(it)
                            }
                        }
                    }

                    if (matchIds.isNotEmpty()) { // Check if matchIds list is not empty
                        val matchesCollection = firestore.collection("TheMatches")
                        val matchesQuery = matchesCollection.whereIn(FieldPath.documentId(), matchIds)

                        matchesQuery.get()
                            .addOnSuccessListener { matchSnapshot ->
                                val matches = mutableListOf<Match>()

                                for (matchDocument in matchSnapshot.documents) {
                                    val match = matchDocument.toObject(Match::class.java)
                                    match?.let {
                                        matches.add(it)
                                    }
                                }

                                displayMatches(matches)
                            }
                            .addOnFailureListener { exception ->
                                Log.e("Firestore", "Error fetching matches: $exception")
                            }
                    } else {
                        Log.d("Firestore", "No valid MatchIds found")
                        // Handle the case where there are no valid MatchIds
                    }
                }
                .addOnFailureListener { exception ->
                    Log.e("Firestore", "Error fetching matchIds: $exception")
                }
        }
    }

    private fun displayMatches(matches: List<Match>) {
        val adapter = MatchAdapter(matches)
        recyclerView.adapter = adapter
    }

    private fun launchActivity(cls: Class<*>) {
        val intent = Intent(this, cls)
        startActivity(intent)
    }
}