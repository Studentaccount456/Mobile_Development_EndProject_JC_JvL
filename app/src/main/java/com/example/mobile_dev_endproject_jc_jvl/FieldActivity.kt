package com.example.mobile_dev_endproject_jc_jvl

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.firestore.FirebaseFirestore
import kotlin.math.log

class FieldActivity : AppCompatActivity() {
    private lateinit var linearLayout: LinearLayout

    private val db = FirebaseFirestore.getInstance()
    private val clubDetailsCollection = db.collection("TheClubDetails")


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.field_screen)

        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottomNavigationView)

        // Right Icon active
        bottomNavigationView.menu.findItem(R.id.navigation_field).isChecked = true

        bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_home -> {
                    launchActivity(HomeActivity::class.java)
                    true
                }

                R.id.navigation_field -> {
                    launchActivity(FieldActivity::class.java)
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

        linearLayout = findViewById(R.id.fieldNames)

        if (linearLayout != null) {
            // Retrieve all documents in the collection
            clubDetailsCollection.get()
                .addOnSuccessListener { querySnapshot ->
                    for (document in querySnapshot) {
                        // Get the ClubName field
                        val clubName = document.getString("ClubName")

                        if (clubName != null) {
                            // Create a TextView for each ClubName
                            val textView = TextView(this)
                            textView.text = clubName

                            // Add the TextView to the LinearLayout
                            linearLayout.addView(textView)
                        }
                    }
                }
        } else {
            Log.e("FieldActivity", "LinearLayout not found")
        }
    }

    private fun launchActivity(cls: Class<*>) {
        val intent = Intent(this, cls)
        startActivity(intent)
    }
}