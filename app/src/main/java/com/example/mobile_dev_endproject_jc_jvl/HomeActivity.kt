package com.example.mobile_dev_endproject_jc_jvl

import android.content.Intent
import android.os.Bundle
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import com.google.android.material.bottomnavigation.BottomNavigationView

class HomeActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.home_screen)

        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottomNavigationView)
        val container = findViewById<LinearLayout>(R.id.container)

        // Right Icon active
        bottomNavigationView.menu.findItem(R.id.navigation_home).isChecked = true

        // Add custom navigation items
        addNavigationItem(container, "Book A court", "Navigates to book a court", AccountActivity::class.java)
        addNavigationItem(container, "Open Option", "Second option", AccountActivity::class.java)

        bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_home -> {
                    launchActivity(HomeActivity::class.java)
                    true
                }
                R.id.navigation_field -> {
                    launchActivity(ClubActivity::class.java)
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

    private fun addNavigationItem(container: LinearLayout, title: String, description: String, targetActivity: Class<*>) {
        val customNavItem = layoutInflater.inflate(R.layout.custom_navigation_item, null) as ConstraintLayout
        val titleTextView = customNavItem.findViewById<TextView>(R.id.titleTextView)
        val descriptionTextView = customNavItem.findViewById<TextView>(R.id.descriptionTextView)

        titleTextView.text = title
        descriptionTextView.text = description

        customNavItem.setOnClickListener {
            launchActivity(targetActivity)
        }

        container.addView(customNavItem)
    }

    private fun launchActivity(cls: Class<*>) {
        val intent = Intent(this, cls)
        startActivity(intent)
    }
}
