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
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.tabs.TabLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class MatchActivity : AppCompatActivity() {

    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
    private lateinit var recyclerView: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.match_screen)

        // Fetch current user's gender from Firestore
        val currentUserUid = FirebaseAuth.getInstance().currentUser?.uid
        if (currentUserUid != null) {
            fetchUserGender(currentUserUid)
        }


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
                        //launchActivity(ClubEstablishmentsActivity::class.java)

                    }

                    1 -> {
                        // Start YourCourtReservationsActivity
                        launchActivity(YourMatchesActivity::class.java)
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
        allMatchesTab.select()

        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottomNavigationView)

        // Right Icon active
        bottomNavigationView.menu.findItem(R.id.navigation_match).isChecked = true

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

        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)

        //fetchDataFromFirestore()
    }

    private fun displayMatches(matches: List<Match>) {
        val adapter = MatchAdapter(matches)
        recyclerView.adapter = adapter
    }

    private fun launchActivity(cls: Class<*>) {
        val intent = Intent(this, cls)
        startActivity(intent)
    }

    private fun fetchUserGender(userId: String) {
        firestore.collection("ThePlayers")
            .document(userId)
            .get()
            .addOnSuccessListener { documentSnapshot ->
                val userGender = documentSnapshot.getString("gender")
                if (userGender != null) {
                    // Now that you have the user's gender, fetch and display matches
                    fetchDataFromFirestore(userGender)
                } else {
                    Log.e("MatchActivity", "User gender not found")
                }
            }
            .addOnFailureListener { exception ->
                Log.e("MatchActivity", "Error fetching user gender: $exception")
            }
    }

    private fun fetchDataFromFirestore(userGender: String) {
        firestore.collection("TheMatches")
            .get()
            .addOnSuccessListener { querySnapshot ->
                val matches = mutableListOf<Match>()

                for (document in querySnapshot.documents) {
                    val match = document.toObject(Match::class.java)
                    Log.d("MatchActivity", "$match")
                    match?.let {
                        // Check if the match should be displayed based on user's gender and gendersAllowed
                        if (shouldDisplayMatch(it, userGender)) {
                            matches.add(it)
                        }
                    }
                }

                displayMatches(matches)
            }
            .addOnFailureListener { exception ->
                Log.e("Firestore", "Error fetching data: $exception")
            }
    }

    private fun shouldDisplayMatch(match: Match, userGender: String): Boolean {
        val gendersAllowed = match.gendersAllowed

        // Check if the match should be displayed based on user's gender and gendersAllowed
        return when (userGender) {
            "Male" -> gendersAllowed != "Female"
            "Female" -> gendersAllowed != "Male"
            else -> true // Handle other cases or use a default value as needed
        }
    }
}