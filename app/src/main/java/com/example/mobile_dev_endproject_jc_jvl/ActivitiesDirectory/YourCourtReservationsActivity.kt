package com.example.mobile_dev_endproject_jc_jvl.ActivitiesDirectory

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.mobile_dev_endproject_jc_jvl.R
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.tabs.TabLayout

class YourCourtReservationsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.your_reservations_courts)
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
                        launchActivity(ClubEstablishmentsActivity::class.java)
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
    }

    private fun launchActivity(cls: Class<*>) {
        val intent = Intent(this, cls)
        startActivity(intent)
    }
}